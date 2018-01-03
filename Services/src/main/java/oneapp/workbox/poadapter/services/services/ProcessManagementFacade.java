package oneapp.workbox.poadapter.services.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.incture.pmc.poadapter.util.LogUtil;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.incture.pmc.poadapter.util.UmeUtil;
import com.incture.pmc.poadapter.util.UserDetailsDto;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.exception.api.BPMException;
import com.sap.bpm.pm.api.ProcessInstance;
import com.sap.bpm.tm.api.TaskDetail;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnerDetailsDto;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * BPM API EJB.Mainly used to get process details using process Id and task
 * details using taskInstance Id.
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@WebService(name = "PMCProcessManagementFacade", portName = "ProcessManagementFacadePort", serviceName = "PMCProcessManagementFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class ProcessManagementFacade implements ProcessManagementFacadeLocal {

	LogUtil logger = new LogUtil(ProcessManagementFacade.class);

	@WebMethod(operationName = "getProcessDetails", exclude = false)
	@Override
	public ProcessEventsDto getProcessDetails(@WebParam(name = "processInstanceId") String processInstanceId) {
		logger.logDebug(
				"[PMC][ProcessManagementFacade][getProcessDetails] Method Initiated with Inputs :" + processInstanceId);
		ProcessEventsDto processEventsDto = new ProcessEventsDto();

		URI processInstanceUri;
		processInstanceUri = URI.create(processInstanceId);
		ProcessInstance processInstance = null;
		try {
			processInstance = BPMFactory.getProcessInstanceManager().getProcessInstance(processInstanceUri);
		} catch (BPMException e) {
			logger.logError(
					"[PMC][ProcessManagementFacade][getProcessDetails] Exception in BPM API due to:" + e.getMessage());
		}
		if (!ServicesUtil.isEmpty(processInstance)) {
			if (!ServicesUtil.isEmpty(processInstance.getRootProcessInstanceId())) {
				processEventsDto.setProcessId(processInstance.getRootProcessInstanceId().toString());
			} else if (!ServicesUtil.isEmpty(processInstance.getParentProcessInstanceId())) {
				processEventsDto.setProcessId(processInstance.getParentProcessInstanceId().toString());
			} else {
				processEventsDto.setProcessId(processInstance.getId().toString());
			}
			String pName = processInstance.getName();
			if (processInstance.getName().equals("Service_Request_Management_Process")) {
				pName = processInstance.getSubject().split(":")[0].trim();
				processEventsDto.setAutomatedIgnored(true);
			}
			processEventsDto.setName(pName);
			processEventsDto.setStartedAt(processInstance.getStartDate());
			processEventsDto.setStatus(processInstance.getStatus().name());
			processEventsDto.setSubject(processInstance.getSubject());
			processEventsDto.setCompletedAt(processInstance.getEndDate());
			processEventsDto.setStartedBy(processInstance.getProcessInitiator().getUniqueName());
			UserDetailsDto user = UmeUtil.getUserDetailsByUserId(processInstance.getProcessInitiator().getUniqueName());
			StringBuffer name = new StringBuffer();
			name = user.getFirstName() == null ? name.append("") : name.append(user.getFirstName()).append(" ");
			name = user.getLastName() == null ? name.append("") : name.append(user.getLastName());
			processEventsDto.setStartedByDisplayName(name.toString());
		}
		logger.logDebug("[PMC][ProcessManagementFacade][getProcessDetails] Exit with response:" + processEventsDto);
		return processEventsDto;
	}

	@WebMethod(operationName = "getTaskDetails", exclude = false)
	@Override
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public TaskEventsDto getTaskDetails(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		logger.logDebug(
				"[PMC][ProcessManagementFacade][getTaskDetails] Method Initiated with Inputs :" + taskInstanceId);
		TaskEventsDto taskEventsDto = new TaskEventsDto();
		URI taskInstanceUri = URI.create(taskInstanceId);
		TaskDetail taskInstance = null;
		try {
			taskInstance = BPMFactory.getTaskInstanceManager().getTaskDetail(taskInstanceUri);
		} catch (BPMException e) {
			logger.logError(
					"[PMC][ProcessManagementFacade][getTaskDetails] Exception in BPM API while getting task Details :"
							+ e.getMessage());
		}
		ProcessInstance processInstance = null;
		try {
			processInstance = BPMFactory.getProcessInstanceManager()
					.getProcessInstanceForTaskInstanceId(taskInstanceUri);
		} catch (BPMException e) {
			logger.logError(
					"[PMC][ProcessManagementFacade][getTaskDetails] Exception in BPM API while getting process Instance from Task Instance :"
							+ e.getMessage());
		}
		if (!ServicesUtil.isEmpty(taskInstance)) {
			taskEventsDto.setCompletedAt(taskInstance.getCompletedTime());
			taskEventsDto.setCompletionDeadLine(taskInstance.getCompleteByTime());
			taskEventsDto.setCreatedAt(taskInstance.getCreatedTime());
			taskEventsDto.setName(taskInstance.getName());
			taskEventsDto.setSubject(taskInstance.getSubject());
			taskEventsDto.setDescription(taskInstance.getDescription());
			taskEventsDto.setPriority(taskInstance.getPriority().name());
			taskEventsDto.setStatus(taskInstance.getStatus().name());
			taskEventsDto.setTaskType("Human");
			//taskEventsDto.setUrl(BPMFactory.getTaskInstanceManager().generateTaskExecutionUrl(taskInstanceUri).toString());
			if (!ServicesUtil.isEmpty(processInstance)) {
				if (!ServicesUtil.isEmpty(processInstance.getRootProcessInstanceId())) {
					taskEventsDto.setProcessId(processInstance.getRootProcessInstanceId().toString());
				} else if (!ServicesUtil.isEmpty(processInstance.getParentProcessInstanceId())) {
					taskEventsDto.setProcessId(processInstance.getParentProcessInstanceId().toString());
				} else {
					taskEventsDto.setProcessId(processInstance.getId().toString());
				}
				if (!ServicesUtil.isEmpty(processInstance.getName()))
					taskEventsDto.setProcessName(processInstance.getName());
			}
			taskEventsDto.setEventId(taskInstanceId);
			if (!ServicesUtil.isEmpty(taskInstance.getActualOwner())) {
				taskEventsDto.setCurrentProcessor(taskInstance.getActualOwner().getUniqueName());
				UserDetailsDto user = UmeUtil.getUserDetailsByUserId(taskInstance.getActualOwner().getUniqueName());
				StringBuffer name = new StringBuffer();
				name = user.getFirstName() == null ? name.append("") : name.append(user.getFirstName()).append(" ");
				name = user.getLastName() == null ? name.append("") : name.append(user.getLastName());
				taskEventsDto.setCurrentProcessorDisplayName(name.toString());
			} else if (!ServicesUtil.isEmpty(taskInstance.getPotentialOwners())) {
				@SuppressWarnings("unchecked")
				Set<IPrincipal> iPrincipals = (Set<IPrincipal>) taskInstance.getPotentialOwners();
				List<TaskOwnerDetailsDto> owners = new ArrayList<TaskOwnerDetailsDto>();
				for (IPrincipal iPrincipal : iPrincipals) {
					TaskOwnerDetailsDto owner = new TaskOwnerDetailsDto();
					IUser taskOwner = (IUser) iPrincipal;
					owner.setLogonId(taskOwner.getUniqueName());
					StringBuffer name = new StringBuffer();
					name = taskOwner.getFirstName() == null ? name.append("")
							: name.append(taskOwner.getFirstName()).append(" ");
					name = taskOwner.getLastName() == null ? name.append("") : name.append(taskOwner.getLastName());
					owner.setDisplayName(name.toString());
					owner.setOwnerEmail(taskOwner.getEmail());
					owners.add(owner);
				}
				taskEventsDto.setOwners(owners);
			}
			// String key = dbLocal.getBusinessKey(processInstance.getName());
			DataObject dataObjectInput = taskInstance.getInputDataObject();
			if (dataObjectInput != null) {
				Type typeInput = dataObjectInput.getType();
				if (typeInput != null) {
					if (typeInput.getProperty("requestId") != null) {
						taskEventsDto.setRequestId(dataObjectInput.getString(typeInput.getProperty("requestId")));
					}
					if (typeInput.getProperty("mode") != null) {
						taskEventsDto.setTaskMode(dataObjectInput.getString(typeInput.getProperty("mode")));
					}
					if (typeInput.getProperty("statusFlag") != null) {
						taskEventsDto.setStatusFlag(dataObjectInput.getString(typeInput.getProperty("statusFlag")));
					}
				}
			}
		}
		logger.logDebug("[PMC][ProcessManagementFacade][getTaskDetails] Exit with response:" + taskEventsDto);
		return taskEventsDto;
	}
}
