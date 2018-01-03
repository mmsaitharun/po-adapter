package oneapp.workbox.poadapter.services.services;

import java.util.List;
import javax.ejb.Local;
import com.incture.pmc.poadapter.dto.NoteDto;


@Local
public interface WorkBoxActionFacadeLocal {

	boolean claimTask(String taskInstanceId);

	boolean release(String taskInstanceId);

	boolean delegate(String taskInstanceId, String userId);

	String addNote(String taskInstanceId, String content);

	List<NoteDto> getNotes(String taskInstanceId);

	String claimAndDelegate(String taskInstanceId, String userId);

	String nominate(String taskInstanceId, String userId);

	boolean complete(String taskInstanceId, String action);

	// URL generateTaskExecutionURL(String taskInstanceId);

}
