package oneapp.workbox.poadapter.services.services;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;

@Local
public interface ProcessManagementFacadeLocal {

	ProcessEventsDto getProcessDetails(String processInstanceId);

	TaskEventsDto getTaskDetails(String taskInstanceId);
}
