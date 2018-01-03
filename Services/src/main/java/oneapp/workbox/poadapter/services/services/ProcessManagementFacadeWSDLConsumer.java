package oneapp.workbox.poadapter.services.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.ws.WebServiceRef;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnerDetailsDto;
import com.incture.pmc.poadapter.util.LogUtil;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.sap.bpm.api.BPMFactory;

/**
 * This EJB is mainly used to provide authentication in consumer proxies for the Event MDB. PMCProcessManagementFacade exposed as web service and consumed in this EJB such that it can get
 * authenticated internally through server
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@Stateless
public class ProcessManagementFacadeWSDLConsumer implements ProcessManagementFacadeWSDLConsumerLocal {

	@WebServiceRef
	private PMCProcessManagementFacadeService pmcProcessServices;

	LogUtil logger = new LogUtil(ProcessManagementFacadeWSDLConsumer.class);

	@Override
	public ProcessEventsDto getProcessDetails(String processInstanceId) {
		logger.logDebug("[PMC][ProcessManagementFacadeWSDLConsumer][getProcessDetails] Method Initiated with Inputs :" + processInstanceId);
		ProcessEventsDto processDto = new ProcessEventsDto();
		com.incture.pmc.poadapter.services.ProcessEventsDto response = pmcProcessServices.getProcessManagementFacadePort().getProcessDetails(processInstanceId.toString());
		if (!ServicesUtil.isEmpty(response)) {
			processDto.setProcessId(response.getProcessId());
			if (!ServicesUtil.isEmpty(response.getCompletedAt())) {
				processDto.setCompletedAt(response.getCompletedAt().toGregorianCalendar().getTime());
			}
			if (!ServicesUtil.isEmpty(response.getStartedAt())) {
				processDto.setStartedAt(response.getStartedAt().toGregorianCalendar().getTime());
			}
			processDto.setName(response.getName());
			processDto.setStartedBy(response.getStartedBy());
			processDto.setStatus(response.getStatus());
			processDto.setSubject(response.getSubject());
			processDto.setStartedByDisplayName(response.getStartedByDisplayName());
			processDto.setRequestId(response.getRequestId());
		}
		logger.logDebug("[PMC][ProcessManagementFacadeWSDLConsumer][getProcessDetails] Exit with Response:" + processDto);
		return processDto;
	}

	@Override
	public TaskEventsDto getTaskDetails(String taskInstanceId) {
		logger.logDebug("[PMC][ProcessManagementFacadeWSDLConsumer][getTaskDetails] Method Initiated with Inputs :" + taskInstanceId);
		TaskEventsDto taskDto = new TaskEventsDto();
		URI taskInstance;
		com.incture.pmc.poadapter.services.TaskEventsDto response = pmcProcessServices.getProcessManagementFacadePort().getTaskDetails(taskInstanceId.toString());
		if (!ServicesUtil.isEmpty(response)) {
			if (!ServicesUtil.isEmpty(response.getCompletedAt())) {
				taskDto.setCompletedAt(response.getCompletedAt().toGregorianCalendar().getTime());
			}
			if (!ServicesUtil.isEmpty(response.getCompletionDeadLine())) {
				taskDto.setCompletionDeadLine(response.getCompletionDeadLine().toGregorianCalendar().getTime());
			}
			if (!ServicesUtil.isEmpty(response.getCreatedAt())) {
				taskDto.setCreatedAt(response.getCreatedAt().toGregorianCalendar().getTime());
			}
			taskDto.setCurrentProcessor(response.getCurrentProcessor());
			taskDto.setCurrentProcessorDisplayName(response.getCurrentProcessorDisplayName());
			taskDto.setName(response.getName());
			taskDto.setSubject(response.getSubject());
			taskDto.setDescription(response.getDescription());
			taskDto.setEventId(response.getEventId());
			taskDto.setProcessName(response.getProcessName());
			taskDto.setTaskMode(response.getTaskMode());
			taskDto.setTaskType(response.getTaskType());
			taskDto.setStatusFlag(response.getStatusFlag());

			List<TaskOwnerDetailsDto> owners = new ArrayList<TaskOwnerDetailsDto>();
			for (com.incture.pmc.poadapter.services.TaskOwnerDetailsDto taskOwnerDetailsDto : response.getOwners()) {
				TaskOwnerDetailsDto dto = new TaskOwnerDetailsDto();
				dto.setLogonId(taskOwnerDetailsDto.getLogonId());
				dto.setDisplayName(taskOwnerDetailsDto.getDisplayName());
				dto.setOwnerEmail(taskOwnerDetailsDto.getOwnerEmail());
				owners.add(dto);
			}
			taskDto.setOwners(owners);
			taskDto.setPriority(response.getPriority());
			taskDto.setProcessId(response.getProcessId());
			taskDto.setStatus(response.getStatus());
			taskDto.setRequestId(response.getRequestId());
			taskDto.setOrigin("BPM");
			try{
				taskInstance = new URI(taskInstanceId);
				taskDto.setUrl(BPMFactory.getTaskInstanceManager().generateTaskExecutionUrl(taskInstance).toString());
			}catch(URISyntaxException e){
				System.err.println("[PMC][ProcessManagementFacadeWSDLConsumer][getTaskDetails][setUrl][URIerrorMessage] "+e.getMessage() );
				e.printStackTrace();
			}
		}
		logger.logDebug("[PMC][ProcessManagementFacadeWSDLConsumer][getTaskDetails] Exit with response :" + taskDto);
		return taskDto;
	}

}
