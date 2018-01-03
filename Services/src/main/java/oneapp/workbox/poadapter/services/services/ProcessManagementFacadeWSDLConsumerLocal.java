package oneapp.workbox.poadapter.services.services;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;

public interface ProcessManagementFacadeWSDLConsumerLocal {

	ProcessEventsDto getProcessDetails(String processInstanceId);

	TaskEventsDto getTaskDetails(String taskInstanceId);

}
