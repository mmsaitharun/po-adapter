package oneapp.workbox.poadapter.services.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.incture.pmc.poadapter.dto.NoteDto;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.tm.api.Note;
import com.sap.bpm.tm.api.TaskInstanceManager;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;

import commonj.sdo.DataObject;

/**
 * BPM API EJB.Mainly used to take work box actions using taskInstance Id.
 * 
 * @version 1.0
 * @since 2017-05-09
 */

@WebService(name = "WorkBoxActionFacade", portName = "WorkBoxActionFacadePort", serviceName = "WorkBoxActionFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class WorkBoxActionFacade implements WorkBoxActionFacadeLocal {
	/**
	 * Default constructor.
	 */
	
	public WorkBoxActionFacade() {
	}
	@WebMethod(operationName = "claimTask", exclude = false)
	@Override
	public boolean claimTask(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][claimTask] method invoked" );
		URI taskInstance;
		try {
			taskInstance = new URI(taskInstanceId);
			BPMFactory.getTaskInstanceManager().claim(taskInstance);
			return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][claimTask][errorMessage] "+e.getMessage() );
		}
		return false;
	}
	@WebMethod(operationName = "release", exclude = false)
	@Override
	public boolean release(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		URI taskInstance;
		try {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][release][errorMessage] method invoked" );
			taskInstance = new URI(taskInstanceId);
			BPMFactory.getTaskInstanceManager().release(taskInstance);
			return true;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][release][errorMessage] "+e.getMessage() );
		}
		return false;
	}
	@WebMethod(operationName = "delegate", exclude = false)
	@Override
	public boolean delegate(@WebParam(name = "taskInstanceId") String taskInstanceId, @WebParam(name = "userId") String userId) {
		URI taskInstance;
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][delegate] method invoked with user"+ userId);
		try {
			taskInstance = new URI(taskInstanceId);
			IUser newOwner = UMFactory.getUserFactory().getUserByUniqueName(userId);
			BPMFactory.getTaskInstanceManager().delegate(taskInstance, newOwner);
			return true;
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][delegate][URIerrorMessage] "+e.getMessage() );
			e.printStackTrace();
		} catch (UMException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][delegate][UMEerrorMessage] "+e.getMessage() );
			e.printStackTrace();
		}
		return false;
	}

	@WebMethod(operationName = "addNote", exclude = false)
	@Override
	public String addNote(@WebParam(name = "taskInstanceId") String taskInstanceId, @WebParam(name = "content") String content) {
		URI tid;
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][addNote] method invoked" );
		try {
			tid = new URI(taskInstanceId);
			URI noteId = BPMFactory.getTaskInstanceManager().addNote(tid, content, false);
			return noteId.toString();
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][addNote][error] "+e.getMessage() );
			e.printStackTrace();
		}
		return null;

	}
	
	@WebMethod(operationName = "complete", exclude = false)
	@Override
	public boolean complete(@WebParam(name = "taskInstanceId") String taskInstanceId ,@WebParam(name = "action") String action) {
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][complete] method invoked with id "+ taskInstanceId);
		try {
		URI tid = new URI(taskInstanceId);
		TaskInstanceManager taskInstanceManager = BPMFactory.getTaskInstanceManager();
		DataObject taskOutput = taskInstanceManager.getTaskDetail(tid).getOutputDataObject();
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][complete][taskOutput]"+ taskOutput );
		if(action.equals("Accept")){
			taskOutput.set("isApproved",true);
		}
		else if(action.equals("Reject")){
			taskOutput.set("isApproved",false);
		}
		taskInstanceManager.complete(tid, taskOutput);
		return true;
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][complete][error] "+e.getMessage() );
			e.printStackTrace();
		}
		return false;
	}

	@WebMethod(operationName = "getNotes", exclude = false)
	@Override
	public List<NoteDto> getNotes(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		URI tid;
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][getNotes] method invoked with"+ taskInstanceId);
		try {
			tid = new URI(taskInstanceId);
			List<NoteDto> dtoList = new ArrayList<NoteDto>();
			List<Note> notesList = BPMFactory.getTaskInstanceManager().getNotes(tid);
			for(Note note : notesList){
				NoteDto dto = new NoteDto();
				dto.setContent(note.getContent());
				dto.setCreatedOn(note.getCreatedOn());
				dto.setId(note.getId().toString());
				dto.setCreatedBy(note.getCreatedBy().getDisplayName());
				dtoList.add(dto);
			}
			return dtoList;
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][getNotes][error] "+e.getMessage() );
			e.printStackTrace();
		}
		return null;

	}
	
	@WebMethod(operationName = "claimAndDelegate", exclude = false)
	@Override
	public String claimAndDelegate(@WebParam(name = "taskInstanceId") String taskInstanceId, @WebParam(name = "userId") String userId) {

		System.err.println("[PMC][WorkBoxAction][services][claimAndDelegate] method invoked with input taskInstanceId:" + taskInstanceId +"userId:"+userId);
		Boolean claimResponse = this.claimTask(taskInstanceId);
		if (claimResponse) {
			
			Boolean delegateResponse = this.delegate(taskInstanceId, userId);
			if(delegateResponse){
				return "SUCCESS";
			}
			else{
				return "FAILURE";
			}
		}
		else{
			return "FAILURE";
		}
	}
	
	@WebMethod(operationName = "nominate", exclude = false)
	@Override
	public String nominate(@WebParam(name = "taskInstanceId") String taskInstanceId,@WebParam(name = "userId") String userId) {
		URI taskInstance;
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][nominate] method invoked with user:"+ userId+"and taskInstanceId:"+taskInstanceId);
		try {
			taskInstance = new URI(taskInstanceId);
			IUser newOwner = UMFactory.getUserFactory().getUserByUniqueName(userId);
			BPMFactory.getTaskInstanceManager().nominate(taskInstance, newOwner);
			return "SUCCESS";
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][nominate][URIerrorMessage] "+e.getMessage() );
			e.printStackTrace();
		} catch (UMException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][nominate][UMEerrorMessage] "+e.getMessage() );
			e.printStackTrace();
		}
		return "FAILURE";
	}
	
	/*@WebMethod(operationName = "generateURL", exclude = false)
	@Override
	public URL generateTaskExecutionURL(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		URI taskInstance;
		System.err.println("[PMC][POAdapterServices][WorkBoxFacade][generateURL] method invoked with taskInstanceId:"+taskInstanceId);
		try {
			taskInstance = new URI(taskInstanceId);
			return BPMFactory.getTaskInstanceManager().generateTaskExecutionUrl(taskInstance);
		} catch (URISyntaxException e) {
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][generateURL][URIerrorMessage] "+e.getMessage() );
			e.printStackTrace();
		}
		return null;
	}*/
}
