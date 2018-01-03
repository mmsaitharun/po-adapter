package oneapp.workbox.poadapter.services.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnerDetailsDto;
import com.incture.pmc.poadapter.dto.ProcessExportFilterCriteria;
import com.incture.pmc.poadapter.util.LogUtil;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.incture.pmc.poadapter.util.UmeUtil;
import com.incture.pmc.poadapter.util.UserDetailsDto;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.api.QueryResultParameters;
import com.sap.bpm.exception.api.BPMException;
import com.sap.bpm.pm.api.ProcessInstance;
import com.sap.bpm.pm.api.ProcessInstanceFilterCriteria;
import com.sap.bpm.pm.api.ProcessInstanceFilterOperator;
import com.sap.bpm.pm.api.ProcessInstanceProperty;
import com.sap.bpm.pm.api.ProcessStatus;
import com.sap.bpm.tm.api.Status;
import com.sap.bpm.tm.api.TaskAbstract;
import com.sap.bpm.tm.api.TaskDetail;
import com.sap.bpm.tm.api.TaskInstanceManager;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * BPM API EJB.Mainly used to get all the running process and task instances.
 * Injection of DatabaseManagementFacadeLocal is done to store all the events
 * obtained
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@WebService(name = "PMCProcessDataExportManagementFacade", portName = "ProcessDataExportManagementFacadePort", serviceName = "PMCProcessDataExportManagementFacadeService", targetNamespace = "http://oneapp.com/incture/pmc/poadapter/services/")
@Stateless
public class ProcessDataExportManagementFacade implements
		ProcessDataExportManagementFacadeLocal {

	@EJB
	private DatabaseManagementFacadeLocal dbLocal;
	

	LogUtil logger = new LogUtil(ProcessDataExportManagementFacade.class);

	@WebMethod(operationName = "getAllRunningInstances", exclude = false)
	public List<ProcessEventsDto> getAllRunningInstances(
			@WebParam(name = "processFilterCriteria") ProcessExportFilterCriteria processFilterCriteria) {
		logger
				.logDebug("[PMC][ProcessDataExportManagementFacade][getAllRunningInstances] Method Initiated with Inputs :"
						+ processFilterCriteria);
		List<ProcessEventsDto> processEvents = new ArrayList<ProcessEventsDto>();
		ProcessInstanceFilterCriteria filterCriteria = new ProcessInstanceFilterCriteria(
				ProcessInstanceProperty.STATUS,
				ProcessInstanceFilterOperator.EQUALS,
				ProcessStatus.IN_PROGRESS, ProcessStatus.SUSPENDED);
		QueryResultParameters resultParameters = new QueryResultParameters();
		resultParameters.setMaxResults(processFilterCriteria.getMaxResults());
		ProcessInstanceFilterCriteria filterCriteria1 = new ProcessInstanceFilterCriteria(
				ProcessInstanceProperty.START_DATE,
				ProcessInstanceFilterOperator.FROM, processFilterCriteria
						.getStartDateFrom());
		ProcessInstanceFilterCriteria filterCriteria2 = new ProcessInstanceFilterCriteria(
				ProcessInstanceProperty.START_DATE,
				ProcessInstanceFilterOperator.TO, processFilterCriteria
						.getStartDateTo());
		List<ProcessInstance> processInstances = null;
		try {
			processInstances = BPMFactory.getProcessInstanceManager()
					.getProcessInstances(resultParameters, filterCriteria,
							filterCriteria1, filterCriteria2);
		} catch (BPMException e) {
			logger
					.logError("[PMC][ProcessDataExportManagementFacade][getAllRunningInstances] Exception in BPM Process API due to:"
							+ e.getMessage());
		}
		for (Iterator<ProcessInstance> iterator2 = processInstances.iterator(); iterator2
				.hasNext();) {

			ProcessEventsDto processEventsDto = new ProcessEventsDto();
			ProcessInstance processInstance = (ProcessInstance) iterator2
					.next();
			if (!ServicesUtil.isEmpty(processInstance
					.getParentProcessInstanceId())
					|| !ServicesUtil.isEmpty(processInstance
							.getRootProcessInstanceId())) {
				logger
						.logInfo("[PMC][ProcessDataExportManagementFacade][getAllRunningInstances] Proccess Instance: "
								+ processInstance.getId().toString()
								+ " is Sub Process for: "
								+ processInstance.getRootProcessInstanceId()
										.toString()
								+ " or "
								+ processInstance.getParentProcessInstanceId()
										.toString());
			} else {
				processEventsDto.setProcessId(processInstance.getId()
						.toString());
				processEventsDto.setName(processInstance.getName());
				processEventsDto.setStartedAt(processInstance.getStartDate());
				processEventsDto.setStatus(processInstance.getStatus().name());
				processEventsDto.setSubject(processInstance.getSubject());
				processEventsDto.setCompletedAt(processInstance.getEndDate());
				if (!ServicesUtil
						.isEmpty(processInstance.getProcessInitiator())) {
					processEventsDto.setStartedBy(processInstance
							.getProcessInitiator().getUniqueName());
					UserDetailsDto user = UmeUtil
							.getUserDetailsByUserId(processInstance
									.getProcessInitiator().getUniqueName());
					StringBuffer name = new StringBuffer();
					name = user.getFirstName() == null ? name.append("") : name.append(
							user.getFirstName()).append(" ");
					name = user.getLastName() == null ? name.append("") : name
							.append(user.getLastName());
					processEventsDto.setStartedByDisplayName(name.toString());
				}
			}
			TaskInstanceManager taskInstanceManager = BPMFactory
					.getTaskInstanceManager();
			HashSet<Status> statuses = new HashSet<Status>();
			statuses.add(Status.READY);
			statuses.add(Status.RESERVED);
			statuses.add(Status.IN_PROGRESS);
			statuses.add(Status.COMPLETED);
			Set<TaskAbstract> taskAbstracts = null;
			try {
				taskAbstracts = taskInstanceManager.getTaskAbstractsByParent(
						processInstance.getId(), statuses);
			} catch (BPMException e) {
				logger
						.logError("[PMC][ProcessDataExportManagementFacade][getAllRunningInstances] Exception in BPM Task API due to:"
								+ e.getMessage());
			}
			List<TaskEventsDto> taskEvents = new ArrayList<TaskEventsDto>();
			for (Iterator<TaskAbstract> iterator3 = taskAbstracts.iterator(); iterator3
					.hasNext();) {
				TaskAbstract taskAbstract = (TaskAbstract) iterator3.next();
				TaskEventsDto taskEventsDto = new TaskEventsDto();
				TaskDetail taskInstance = taskInstanceManager
						.getTaskDetail(taskAbstract.getId());
				taskEventsDto.setEventId(taskAbstract.getId().toString());
				taskEventsDto.setCompletedAt(taskInstance.getCompletedTime());
				taskEventsDto.setCompletionDeadLine(taskInstance
						.getCompleteByTime());
				taskEventsDto.setCreatedAt(taskInstance.getCreatedTime());
				taskEventsDto.setName(taskInstance.getName());
				taskEventsDto.setSubject(taskInstance.getSubject());
				taskEventsDto.setDescription(taskInstance.getDescription());
				taskEventsDto.setPriority(taskInstance.getPriority().name());
				taskEventsDto.setStatus(taskInstance.getStatus().name());
				if (!ServicesUtil.isEmpty(processInstance)) {
					if (!ServicesUtil.isEmpty(processInstance
							.getRootProcessInstanceId())) {
						taskEventsDto.setProcessId(processInstance
								.getRootProcessInstanceId().toString());
					} else if (!ServicesUtil.isEmpty(processInstance
							.getParentProcessInstanceId())) {
						taskEventsDto.setProcessId(processInstance
								.getParentProcessInstanceId().toString());
					} else {
						taskEventsDto.setProcessId(processInstance.getId()
								.toString());
					}
				}
				if (!ServicesUtil.isEmpty(taskInstance.getActualOwner())) {
					taskEventsDto.setCurrentProcessor(taskInstance.getActualOwner().getUniqueName());
					UserDetailsDto currentProc = UmeUtil.getUserDetailsByUserId(taskInstance.getActualOwner().getUniqueName());
					StringBuffer currentProc1 = new StringBuffer();
					currentProc1 = currentProc.getFirstName() == null ? currentProc1.append("")
							: currentProc1.append(currentProc.getFirstName()).append(" ");
					currentProc1 = currentProc.getLastName() == null ? currentProc1.append("")
							: currentProc1.append(currentProc.getLastName());
					taskEventsDto.setCurrentProcessorDisplayName(currentProc1.toString());
				} else if (!ServicesUtil.isEmpty(taskInstance.getPotentialOwners())) {
					List<TaskOwnerDetailsDto> owners = new ArrayList<TaskOwnerDetailsDto>(); 
					for (IPrincipal iPrincipal : taskInstance.getPotentialOwners()) {
						TaskOwnerDetailsDto owner = new TaskOwnerDetailsDto();
						IUser taskOwner = (IUser) iPrincipal;
						owner.setLogonId(taskOwner.getUniqueName());
						StringBuffer name1 = new StringBuffer();
						name1 = taskOwner.getFirstName() == null ? name1.append("")
								: name1.append(taskOwner.getFirstName()).append(" ");
						name1 = taskOwner.getLastName() == null ? name1.append("")
								: name1.append(taskOwner.getLastName());
						owner.setDisplayName(name1.toString());
						owners.add(owner);
					}
					taskEventsDto.setOwners(owners);
				}
			//	 String key = configLocal.getBusinessKey(processInstance.getName());
					DataObject dataObjectInput = taskInstance.getInputDataObject();
					if (dataObjectInput != null) {
						Type typeInput = dataObjectInput.getType();
						if (typeInput != null) {
							if (typeInput.getProperty("requestId") != null) {
								taskEventsDto.setRequestId(dataObjectInput
										.getString(typeInput.getProperty("requestId")));
							}
						}
					}
				taskEvents.add(taskEventsDto);
			}
			processEventsDto.setTaskEvents(taskEvents);
			processEvents.add(processEventsDto);
		}
		dbLocal.createProcesses(processEvents);
		logger
				.logDebug("[PMC][ProcessDataExportManagementFacade][getAllRunningInstances] Exit");
		return processEvents;
	}
}
