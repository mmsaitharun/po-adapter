package oneapp.workbox.poadapter.hana.connector;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnersDto;

@Local
public interface HanaDBUpdateManagerLocal {

	public void saveAndUpdateProcessEvent(ProcessEventsDto processEventsDto);

	void updateProcessEvents(ProcessEventsDto processEventsDto);

	public ProcessEventsDto readProcessEvent(String processId);

	public void saveAndUpdateTask(TaskEventsDto taskEventsDto, boolean isForwarded);

	public TaskEventsDto readTaskEvent(String processId, String eventId);

	public TaskOwnersDto readTaskOwner(String eventId, String taskOwner);

	void createProcess(ProcessEventsDto processEventsDto);

	void createProcesses(List<ProcessEventsDto> processes);

	void updateTaskEvents(String taskInstanceId);

	public void updateAutomatedTask(String processId, Date CompletedDate);

	List<String> getAllProcessName();

}
