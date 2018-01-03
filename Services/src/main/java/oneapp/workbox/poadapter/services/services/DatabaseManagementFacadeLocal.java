package oneapp.workbox.poadapter.services.services;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnersDto;

@Local
public interface DatabaseManagementFacadeLocal {

	public ProcessEventsDto readProcessEvent(String processId);

	public void saveAndUpdateProcessEvent(ProcessEventsDto processEventsDto);

	public TaskEventsDto readTaskEvent(String processId, String eventId);

	public void saveAndUpdateTask(TaskEventsDto taskEventsDto, boolean isForwarded);

	public TaskOwnersDto readTaskOwner(String eventId, String taskOwner);

	void createProcess(ProcessEventsDto processEventsDto);

	void createProcesses(List<ProcessEventsDto> processes);

	void updateTaskEvents(String taskInstanceId);

	void updateProcessEvents(ProcessEventsDto processEventsDto);

	public void updateAutomatedTask(String processId, Date date);

	List<String> getAllProcessName();


}
