package oneapp.workbox.poadapter.hana.connector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.ResponseDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnerDetailsDto;
import com.incture.pmc.poadapter.dto.TaskOwnersDto;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.MessageUIDto;
import com.incture.pmc.poadapter.util.NoResultFault;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.incture.pmc.poadapter.util.UmeUtil;

/**
 * @author Sai.Tharun
 *
 */
public class HanaDBDao {
	
	HanaDBConnector con;
	
	/**
	 * Default Constructor
	 */
	public HanaDBDao() {
	}
	
	public HanaDBDao(HanaDBConnector con) {
		this.con = con;
	}
	
	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 
	 * Operations over Process Events Table
	 */
	
	public ResponseDto insertIntoProcessEvents(ProcessEventsDto processEventsDto) {
		ResponseDto responseDto = new ResponseDto();
		int result = 0;
		
		String insertQuery = "INSERT INTO PROCESS_EVENTS(PROCESS_ID, COMPLETED_AT, NAME, REQUEST_ID, STARTED_AT, STARTED_BY, STARTED_BY_DISP, STATUS, SUBJECT) VALUES ('"+processEventsDto.getProcessId()+"', "+null+", '"+processEventsDto.getName()+"', '"+processEventsDto.getRequestId()+"', '"+dateFormatter.format(processEventsDto.getStartedAt())+"', '"+processEventsDto.getStartedBy()+"', '"+processEventsDto.getStartedByDisplayName()+"', '"+processEventsDto.getStatus()+"', '"+processEventsDto.getSubject()+"')";
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoProcessEvents][query] : "+insertQuery);
		
		try {
			result = con.insert(insertQuery);
		} catch (ExecutionFault e) {
			responseDto.setMessage("Insertion Un - Successful with Exception: "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		if(result == 1){
			responseDto.setMessage("Insertion Successful");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} else {
			responseDto.setMessage("Insertion Un - Successful");
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	public ProcessEventsDto readProcessEvent(String processId) throws NoResultFault, ExecutionFault {
		ProcessEventsDto processEventsDto = new ProcessEventsDto();
		ResultSet resultSet = null;
		
		if(!ServicesUtil.isEmpty(processId)){
			String selectProcessEvent = "SELECT * FROM PROCESS_EVENTS WHERE PROCESS_ID = '"+processId+"'";
			try {
				resultSet = con.query(selectProcessEvent);
				processEventsDto = this.getProcessDtoFromResultSet(resultSet);
			} catch (SQLException e) {
				MessageUIDto faultInfo = new MessageUIDto();
				faultInfo.setMessage(e.getMessage());
				String message = "Retrieve of " + processEventsDto.getClass().getSimpleName() + " by keys " + processId
						+ " failed!";
				throw new ExecutionFault(message, faultInfo, e);
			}
			if(ServicesUtil.isEmpty(resultSet)){
				throw new NoResultFault("noRecordFound" + processEventsDto.getClass().getSimpleName() + "#" +processId);
			}
		}
		return processEventsDto;
	}
	
	private ProcessEventsDto getProcessDtoFromResultSet(ResultSet resultSet) {
		ProcessEventsDto processEventsDto = null;
		if(!ServicesUtil.isEmpty(resultSet)){
			try {
				while(resultSet.next()){
					processEventsDto = new ProcessEventsDto();
					processEventsDto.setProcessId(resultSet.getString("PROCESS_ID"));
					processEventsDto.setCompletedAt(resultSet.getDate("COMPLETED_AT"));
					processEventsDto.setName(resultSet.getString("NAME"));
					processEventsDto.setRequestId(resultSet.getString("REQUEST_ID"));
					processEventsDto.setStartedAt(resultSet.getDate("STARTED_AT"));
					processEventsDto.setStartedBy(resultSet.getString("STARTED_BY"));
					processEventsDto.setStartedByDisplayName(resultSet.getString("STARTED_BY_DISP"));
					processEventsDto.setStatus(resultSet.getString("STATUS"));
					processEventsDto.setSubject(resultSet.getString("SUBJECT"));
				}
			} catch (SQLException e) {
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][getProcessDtoFromResultSet][Exception] : "+e.getMessage());
			}
		}
		return processEventsDto;
	}

	public ResponseDto updateProcessEvents(ProcessEventsDto processEventsDto) {
		ResponseDto responseDto = new ResponseDto();
		int result = 0;
		
		ProcessEventsDto eventsDto = new ProcessEventsDto();
		try {
			eventsDto = this.mergeProcessEventsDto(processEventsDto, processEventsDto.getProcessId());
			
			String updateQuery = "UPDATE PROCESS_EVENTS SET PROCESS_ID = '"+eventsDto.getProcessId()+"', COMPLETED_AT = '"+dateFormatter.format(eventsDto.getCompletedAt())+"', NAME = '"+eventsDto.getName()+"', REQUEST_ID = '"+eventsDto.getRequestId()+"', STARTED_AT = '"+dateFormatter.format(eventsDto.getStartedAt())+"', STARTED_BY = '"+eventsDto.getStartedBy()+"', STARTED_BY_DISP = '"+eventsDto.getStartedByDisplayName()+"', STATUS = '"+eventsDto.getStatus()+"', SUBJECT = '"+eventsDto.getSubject()+"' ";
			
			System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateProcessEvents][query] : "+updateQuery);
			
			result = con.update(updateQuery);
			
			if(result == 1){
				responseDto.setMessage("Updation Successful");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");
			} else {
				responseDto.setMessage("Updation Un - Successful");
				responseDto.setStatus("FAILURE");
				responseDto.setStatusCode("1");
			}
			
		} catch (NoResultFault e){
			insertIntoProcessEvents(processEventsDto);
			responseDto.setMessage("No Record Found So Insertion Successful");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (ExecutionFault e) {
			responseDto.setMessage("Updation Un - Successful Exception : "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	private ProcessEventsDto mergeProcessEventsDto(ProcessEventsDto processEventsDto, String processId) throws NoResultFault, ExecutionFault {
		
		ProcessEventsDto eventsDto = new ProcessEventsDto();
		eventsDto = this.readProcessEvent(processId);
		
		if(!ServicesUtil.isEmpty(eventsDto)){
			
			eventsDto.setProcessId(ServicesUtil.isEmpty(processEventsDto.getProcessId()) ? eventsDto.getProcessId() : processEventsDto.getProcessId());
			eventsDto.setRequestId(ServicesUtil.isEmpty(processEventsDto.getRequestId()) ? eventsDto.getRequestId() : processEventsDto.getRequestId());
			eventsDto.setCompletedAt(ServicesUtil.isEmpty(processEventsDto.getCompletedAt()) ? eventsDto.getCompletedAt() : processEventsDto.getCompletedAt());
			eventsDto.setName(ServicesUtil.isEmpty(processEventsDto.getName()) ? eventsDto.getName() : processEventsDto.getName());
			eventsDto.setStartedAt(ServicesUtil.isEmpty(processEventsDto.getStartedAt()) ? eventsDto.getStartedAt() : processEventsDto.getStartedAt());
			eventsDto.setStartedBy(ServicesUtil.isEmpty(processEventsDto.getStartedBy()) ? eventsDto.getStartedBy() : processEventsDto.getStartedBy());
			eventsDto.setStartedByDisplayName(ServicesUtil.isEmpty(processEventsDto.getStartedByDisplayName()) ? eventsDto.getStartedByDisplayName() : processEventsDto.getStartedByDisplayName());
			eventsDto.setStatus(ServicesUtil.isEmpty(processEventsDto.getStatus()) ? eventsDto.getStatus() : processEventsDto.getStatus());
			eventsDto.setSubject(ServicesUtil.isEmpty(processEventsDto.getSubject()) ? eventsDto.getSubject() : processEventsDto.getSubject());
			eventsDto.setTaskEvents(ServicesUtil.isEmpty(processEventsDto.getTaskEvents()) ? eventsDto.getTaskEvents() : processEventsDto.getTaskEvents());
			eventsDto.setAutomatedIgnored(ServicesUtil.isEmpty(processEventsDto.isAutomatedIgnored()) ? eventsDto.isAutomatedIgnored() : processEventsDto.isAutomatedIgnored());
			
		} else {
			throw new NoResultFault("noRecordFound"
				//	+ "" + eventsDto.getClass().getSimpleName() + "#"
							+ " " +processId);
		}
		return eventsDto;
	}
	
	
	
	/**
	 * 
	 * Operations over Task Events Table
	 */
	
	
	public void saveAndUpdateTask(TaskEventsDto taskEventsDto, boolean isForwarded) {
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][saveAndUpdateTask] enter with : "+taskEventsDto);
		if(!ServicesUtil.isEmpty(taskEventsDto) && !ServicesUtil.isEmpty(taskEventsDto.getProcessId().trim())
				&& !ServicesUtil.isEmpty(taskEventsDto.getEventId().trim())){
			try{
				if(isForwarded){
					TaskEventsDto eventsDto = readTaskEvent(taskEventsDto.getEventId());
					taskEventsDto.setForwardedBy(eventsDto.getCurrentProcessor());
					taskEventsDto.setForwardedAt(new Date());
				}
				if(!ServicesUtil.isEmpty(taskEventsDto.getStatusFlag()) && taskEventsDto.getStatusFlag().equals("Reject")){
					if (this.checkStatus(taskEventsDto)) {
						taskEventsDto.setStatusFlag("Approve");
					}
				}
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][saveAndUpdateTask] before updating TaskEvents");
				this.updateTaskEvents(taskEventsDto);
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][saveAndUpdateTask] after updating TaskEvents");
				if(!ServicesUtil.isEmpty(taskEventsDto.getCurrentProcessor())){
					System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][saveAndUpdateTask] current processor : "+taskEventsDto.getCurrentProcessor());
					this.updateProcessedOwner(taskEventsDto.getEventId());
					TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
					taskOwnersDto.setTaskOwner(taskEventsDto.getCurrentProcessor());
					taskOwnersDto.setTaskOwnerDisplayName(taskEventsDto.getCurrentProcessorDisplayName());
					taskOwnersDto.setEventId(taskEventsDto.getEventId());
					taskOwnersDto.setTaskOwnerEmail(UmeUtil.getEmailIdByUserId(taskEventsDto.getCurrentProcessor()));
					taskOwnersDto.setIsProcessed(true);
					try{
						this.updateTaskOwners(taskOwnersDto);
					} catch (NoResultFault e){
						updateProcessedOwner(taskEventsDto.getEventId());
						this.insertIntoTaskOwners(taskOwnersDto);
					}
				} else {
					updateProcessedOwner(taskEventsDto.getEventId());
				}
			} catch(NoResultFault e){
				System.err.println("[PMC][DatabaseManagementFacade][saveAndUpdateTask][NoResultFault]thrown :");
				try {
					this.insertIntoTaskEvents(taskEventsDto);
					updateRequestId(taskEventsDto.getProcessId(), taskEventsDto.getRequestId());
					if (!ServicesUtil.isEmpty(taskEventsDto.getOwners())) {
						for (TaskOwnerDetailsDto owner : taskEventsDto.getOwners()) {
							TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
							taskOwnersDto.setTaskOwner(owner.getLogonId());
							taskOwnersDto.setTaskOwnerDisplayName(owner.getDisplayName());
							taskOwnersDto.setEventId(taskEventsDto.getEventId());
							taskOwnersDto.setIsProcessed(false);
							taskOwnersDto.setTaskOwnerEmail(owner.getOwnerEmail());
							this.insertIntoTaskOwners(taskOwnersDto);
						}
					}
				} catch (Exception e1){
					System.err.println("[PMC][DatabaseManagementFacade][saveAndUpdateTask][Exception One] : "+e.getMessage());
				}
			} catch (Exception e){
				System.err.println("[PMC][DatabaseManagementFacade][saveAndUpdateTask][Exception Two] : "+e.getMessage());
			}
		}
	}
	
	private void updateRequestId(String processId, String requestId) {
		ProcessEventsDto processEventsDto = new ProcessEventsDto();
		//processEventsDto.setProcessId(processId);
		try {
			processEventsDto = this.readProcessEvent(processId);
			processEventsDto.setRequestId(requestId);
			this.updateProcessEvents(processEventsDto);
		} catch (NoResultFault e) {
			System.err.println("[PMC][DatabaseManagementFacade][saveAndUpdateTask][Exception] No Result Fault : "+e.getMessage());
		} catch (ExecutionFault e) {
			System.err.println("[PMC][DatabaseManagementFacade][saveAndUpdateTask][Exception] Execution Fault : "+e.getMessage());
		}
		
	}

	private void updateProcessedOwner(String eventId) throws NoResultFault, SQLException, ExecutionFault {
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateProcessedOwner] started with event id : " +eventId);
		TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
		String queryForStatus = "SELECT * FROM TASK_OWNERS TW WHERE TW.IS_PROCESSED = '1' and TW.EVENT_ID = '"+eventId+"'";
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateProcessedOwner][query] queryForStatus : " +queryForStatus);
//		try{
			ResultSet resultSet = con.query(queryForStatus);
			taskOwnersDto = this.getTaskOwnersDtoFromResultSet(resultSet);
			if(ServicesUtil.isEmpty(taskOwnersDto.getTaskOwner())){
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateProcessedOwner][NoResultFault thrown]");
				throw new NoResultFault("noRecordFound");
			} else {
				TaskOwnersDto ownersDto = new TaskOwnersDto();
				ownersDto.setEventId(taskOwnersDto.getEventId());
				ownersDto.setTaskOwner(taskOwnersDto.getTaskOwner());
				ownersDto.setTaskOwnerDisplayName(taskOwnersDto.getTaskOwnerDisplayName());
				ownersDto.setTaskOwnerEmail(taskOwnersDto.getTaskOwnerEmail());
				ownersDto.setIsProcessed(false);
				this.updateTaskOwners(ownersDto);
			}
//		} catch (Exception e) {
//			System.err.println("[PMC][DatabaseManagementFacade][updateProcessedOwner][Exception]" + e.getMessage());
//		}
	}

	private boolean checkStatus(TaskEventsDto taskEventsDto) {
		TaskEventsDto eventsDto = null;
		try {
			String queryForMax = "SELECT * FROM TASK_EVENTS P WHERE P.CREATED_AT = (SELECT MAX(TASK_EVENTS.CREATED_AT) FROM TASK_EVENTS WHERE TASK_EVENTS.PROCESS_ID = '"
					+ taskEventsDto.getProcessId() + "')";
			eventsDto = new TaskEventsDto();
			ResultSet resultSet = con.query(queryForMax);
			eventsDto = this.getTaskDtoFromResultSet(resultSet);

			if (!ServicesUtil.isEmpty(eventsDto) && eventsDto.getStatusFlag().equals("Reject")
					&& !(eventsDto.getTaskMode().equals(taskEventsDto.getTaskMode())
							&& eventsDto.getName().equals(taskEventsDto.getName()))) {
				return this.checkIsParallel(eventsDto, taskEventsDto);
			} else {
				return false;
			}
		} catch (Exception e) {
			System.err.println("[PMC][DatabaseManagementFacade][checkStatus][Exception]" + e.getMessage());
		}

		return false;
	}

	private boolean checkIsParallel(TaskEventsDto eventsDto, TaskEventsDto taskEventsDto) {
		int countResult = 0;
		if(!ServicesUtil.isEmpty(eventsDto.getTaskMode())&&!ServicesUtil.isEmpty(eventsDto.getName())&&!ServicesUtil.isEmpty(taskEventsDto.getTaskMode())&&!ServicesUtil.isEmpty(taskEventsDto.getName())){
			try{
				String queryForParallelCheck = "Select count(*) from TASK_SLA s left join TASK_STEP_MAPPING ts on S.TASK_SLA_ID = TS.TASK_SLA_ID where S.TASK_MODE = '"+eventsDto.getTaskMode()+"' and s.TASK_DEF = '"+eventsDto.getName()+"' and ts.TASK_MODE = '"+taskEventsDto.getTaskMode()+"' and ts.TASK_DEF = '"+taskEventsDto.getName()+"'";
				ResultSet resultSet = con.query(queryForParallelCheck);
				while(resultSet.next()){
					countResult = resultSet.getInt(1);
				}
				if(countResult == 1){
					return true;
				} else {
					return false;
				}
			} catch(Exception e){
				System.err.println("[PMC][HanaDBDao][checkIsParallel][Exception]" + e.getMessage());
			}
		}
		return false;
	}

	public ResponseDto insertIntoTaskEvents(TaskEventsDto taskEventsDto) {
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][entered] with taskEventsDto: "+taskEventsDto);
		ResponseDto responseDto = new ResponseDto();
		int result = 0;
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][before eQuery]");
		String eQuery = "INSERT INTO TASK_EVENTS(COMPLETED_AT, COMP_DEADLINE, CREATED_AT, CUR_PROC, CUR_PROC_DISP, DESCRIPTION, FORWARDED_AT, FORWARDED_BY, NAME, ORIGIN, PRIORITY, PROC_NAME, STATUS, STATUS_FLAG, SUBJECT, TASK_MODE, TASK_TYPE, URL, EVENT_ID, PROCESS_ID) VALUES ('"+dateFormatter.format(taskEventsDto.getCompletedAt())+"', '"+taskEventsDto.getCompletionDeadLine()+"', '"+dateFormatter.format(taskEventsDto.getCreatedAt())+"', '"+taskEventsDto.getCurrentProcessor()+"', '"+taskEventsDto.getCurrentProcessorDisplayName()+"', '"+taskEventsDto.getDescription()+"', '"+dateFormatter.format(taskEventsDto.getForwardedAt())+"', '"+taskEventsDto.getForwardedBy()+"', '"+taskEventsDto.getName()+"', '"+taskEventsDto.getOrigin()+"', '"+taskEventsDto.getPriority()+"', '"+taskEventsDto.getProcessName()+"', '"+taskEventsDto.getStatus()+"', '"+taskEventsDto.getStatusFlag()+"', '"+taskEventsDto.getSubject()+"', '"+taskEventsDto.getTaskMode()+"', '"+taskEventsDto.getTaskType()+"', '"+taskEventsDto.getUrl()+"', '"+taskEventsDto.getEventId()+"', '"+taskEventsDto.getProcessId()+"')";
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][query] : "+eQuery);
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][before eQuery]");
		
		try {
			String insertQuery = "INSERT INTO TASK_EVENTS(COMPLETED_AT, COMP_DEADLINE, CREATED_AT, CUR_PROC, CUR_PROC_DISP, DESCRIPTION, FORWARDED_AT, FORWARDED_BY, NAME, ORIGIN, PRIORITY, PROC_NAME, STATUS, STATUS_FLAG, SUBJECT, TASK_MODE, TASK_TYPE, URL, EVENT_ID, PROCESS_ID) VALUES ('"+dateFormatter.format(taskEventsDto.getCompletedAt())+"', '"+taskEventsDto.getCompletionDeadLine()+"', '"+dateFormatter.format(taskEventsDto.getCreatedAt())+"', '"+taskEventsDto.getCurrentProcessor()+"', '"+taskEventsDto.getCurrentProcessorDisplayName()+"', '"+taskEventsDto.getDescription()+"', '"+dateFormatter.format(taskEventsDto.getForwardedAt())+"', '"+taskEventsDto.getForwardedBy()+"', '"+taskEventsDto.getName()+"', '"+taskEventsDto.getOrigin()+"', '"+taskEventsDto.getPriority()+"', '"+taskEventsDto.getProcessName()+"', '"+taskEventsDto.getStatus()+"', '"+taskEventsDto.getStatusFlag()+"', '"+taskEventsDto.getSubject()+"', '"+taskEventsDto.getTaskMode()+"', '"+taskEventsDto.getTaskType()+"', '"+taskEventsDto.getUrl()+"', '"+taskEventsDto.getEventId()+"', '"+taskEventsDto.getProcessId()+"')";
			System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][query] : "+insertQuery);
		
			result = con.insert(insertQuery);
		} catch (ExecutionFault e) {
			System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskEvents][EXCEPTION]: "+e.getMessage());
			responseDto.setMessage("Insertion Un - Successful with Exception: "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		if(result == 1){
			responseDto.setMessage("Insertion Successful");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} else {
			responseDto.setMessage("Insertion Un - Successful");
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	public ResponseDto updateTaskEvents(TaskEventsDto taskEventsDto){
		ResponseDto responseDto = new ResponseDto();
		int result = 0;
		
		TaskEventsDto eventsDto = new TaskEventsDto();
		try{
			eventsDto = this.mergeTaskEventsDto(taskEventsDto, taskEventsDto.getEventId());
			
			String updateQuery = "UPDATE TASK_EVENTS SET COMPLETED_AT = '"+dateFormatter.format(eventsDto.getCompletedAt())+"', COMP_DEADLINE = '"+dateFormatter.format(eventsDto.getCompletionDeadLine())+"', CREATED_AT = '"+dateFormatter.format(eventsDto.getCreatedAt())+"', CUR_PROC = '"+eventsDto.getCurrentProcessor()+"', CUR_PROC_DISP = '"+eventsDto.getCurrentProcessorDisplayName()+"', DESCRIPTION = '"+eventsDto.getDescription()+"', FORWARDED_AT = '"+eventsDto.getForwardedAt()+"', FORWARDED_BY = '"+eventsDto.getForwardedBy()+"', NAME = '"+eventsDto.getName()+"', ORIGIN = '"+eventsDto.getOrigin()+"', PRIORITY = '"+eventsDto.getPriority()+"', PROC_NAME = '"+eventsDto.getProcessName()+"', STATUS = '"+eventsDto.getStatus()+"', STATUS_FLAG = '"+eventsDto.getStatusFlag()+"', SUBJECT = '"+eventsDto.getSubject()+"', TASK_MODE = '"+eventsDto.getTaskMode()+"', TASK_TYPE = '"+eventsDto.getTaskType()+"', URL = '"+eventsDto.getUrl()+"', EVENT_ID = '"+eventsDto.getEventId()+"', PROCESS_ID = '"+eventsDto.getProcessId()+"'";
			System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateTaskEvents][query] : "+updateQuery);
			
			result = con.update(updateQuery);
			
			if(result == 1){
				responseDto.setMessage("Updation Successful");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");
			} else {
				responseDto.setMessage("Updation Un - Successful");
				responseDto.setStatus("FAILURE");
				responseDto.setStatusCode("1");
			}
		} catch (NoResultFault e){
			//insertIntoTaskEvents(taskEventsDto);
			responseDto.setMessage("No Record Found So Insertion Successful");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (ExecutionFault e) {
			responseDto.setMessage("Updation Un - Successful Exception : "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	public TaskEventsDto readTaskEvent(String taskId) throws ExecutionFault, NoResultFault {
		TaskEventsDto taskEventsDto = new TaskEventsDto();
		ResultSet resultSet = null;
		
		if(!ServicesUtil.isEmpty(taskId)){
			String selectTaskEvent = "SELECT * FROM TASK_EVENTS WHERE EVENT_ID = '"+taskId+"'";
			try {
				resultSet = con.query(selectTaskEvent);
				taskEventsDto = this.getTaskDtoFromResultSet(resultSet);
			} catch (SQLException e) {
				MessageUIDto faultInfo = new MessageUIDto();
				faultInfo.setMessage(e.getMessage());
				String message = "Retrieve of " + taskEventsDto.getClass().getSimpleName() + " by keys " + taskId
						+ " failed!";
				throw new ExecutionFault(message, faultInfo, e);
			}
			if(ServicesUtil.isEmpty(resultSet)){
				throw new NoResultFault("noRecordFound" + taskEventsDto.getClass().getSimpleName() + "#" +taskId);
			}
		}
		return taskEventsDto;
	}

	private TaskEventsDto getTaskDtoFromResultSet(ResultSet resultSet) {
		TaskEventsDto taskEventsDto = null;
		if(!ServicesUtil.isEmpty(resultSet)){
			try {
				while(resultSet.next()){
					taskEventsDto = new TaskEventsDto();
					taskEventsDto.setCompletedAt(resultSet.getDate("COMPLETED_AT"));
					taskEventsDto.setCompletionDeadLine(resultSet.getDate("COMP_DEADLINE"));
					taskEventsDto.setCreatedAt(resultSet.getDate("CREATED_AT"));
					taskEventsDto.setCurrentProcessor(resultSet.getString("CUR_PROC"));
					taskEventsDto.setCurrentProcessorDisplayName(resultSet.getString("CUR_PROC_DISP"));
					taskEventsDto.setDescription(resultSet.getString("DESCRIPTION"));
					taskEventsDto.setForwardedAt(resultSet.getDate("FORWARDED_AT"));
					taskEventsDto.setForwardedBy(resultSet.getString("FORWARDED_BY"));
					taskEventsDto.setName(resultSet.getString("NAME"));
					taskEventsDto.setOrigin(resultSet.getString("ORIGIN"));
					taskEventsDto.setPriority(resultSet.getString("PRIORITY"));
					taskEventsDto.setProcessName(resultSet.getString("PROC_NAME"));
					taskEventsDto.setStatus(resultSet.getString("STATUS"));
					taskEventsDto.setStatusFlag(resultSet.getString("STATUS_FLAG"));
					taskEventsDto.setSubject(resultSet.getString("SUBJECT"));
					taskEventsDto.setTaskMode(resultSet.getString("TASK_MODE"));
					taskEventsDto.setTaskType(resultSet.getString("TASK_TYPE"));
					taskEventsDto.setUrl(resultSet.getString("URL"));
					taskEventsDto.setEventId(resultSet.getString("EVENT_ID"));
					taskEventsDto.setProcessId(resultSet.getString("PROCESS_ID"));
				}
			} catch (SQLException e) {
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][getTaskDtoFromResultSet][Exception] : "+e.getMessage());
			}
		}
		return taskEventsDto;
	}

	private TaskEventsDto mergeTaskEventsDto(TaskEventsDto taskEventsDto, String eventId) throws ExecutionFault, NoResultFault {
		
		TaskEventsDto eventsDto = new TaskEventsDto();
		eventsDto = this.readTaskEvent(eventId);
		
		if(!ServicesUtil.isEmpty(eventsDto)){
			
			eventsDto.setCompletedAt(ServicesUtil.isEmpty(taskEventsDto.getCompletedAt()) ? eventsDto.getCompletedAt() : taskEventsDto.getCompletedAt());
			eventsDto.setCompletionDeadLine(ServicesUtil.isEmpty(taskEventsDto.getCompletionDeadLine()) ? eventsDto.getCompletionDeadLine() : taskEventsDto.getCompletionDeadLine());
			eventsDto.setCreatedAt(ServicesUtil.isEmpty(taskEventsDto.getCreatedAt()) ? eventsDto.getCreatedAt() : taskEventsDto.getCreatedAt());
			eventsDto.setCurrentProcessor(ServicesUtil.isEmpty(taskEventsDto.getCurrentProcessor()) ? eventsDto.getCurrentProcessor() : taskEventsDto.getCurrentProcessor());
			eventsDto.setCurrentProcessorDisplayName(ServicesUtil.isEmpty(taskEventsDto.getCurrentProcessorDisplayName()) ? eventsDto.getCurrentProcessorDisplayName() : taskEventsDto.getCurrentProcessorDisplayName());
			eventsDto.setDescription(ServicesUtil.isEmpty(taskEventsDto.getDescription()) ? eventsDto.getDescription() : taskEventsDto.getDescription());
			eventsDto.setForwardedAt(ServicesUtil.isEmpty(taskEventsDto.getForwardedAt()) ? eventsDto.getForwardedAt() : taskEventsDto.getForwardedAt());
			eventsDto.setForwardedBy(ServicesUtil.isEmpty(taskEventsDto.getForwardedBy()) ? eventsDto.getForwardedBy() : taskEventsDto.getForwardedBy());
			eventsDto.setName(ServicesUtil.isEmpty(taskEventsDto.getName()) ? eventsDto.getName() : taskEventsDto.getName());
			eventsDto.setOrigin(ServicesUtil.isEmpty(taskEventsDto.getOrigin()) ? eventsDto.getOrigin() : taskEventsDto.getOrigin());
			eventsDto.setPriority(ServicesUtil.isEmpty(taskEventsDto.getPriority()) ? eventsDto.getPriority() : taskEventsDto.getPriority());
			eventsDto.setProcessName(ServicesUtil.isEmpty(taskEventsDto.getProcessName()) ? eventsDto.getProcessName() : taskEventsDto.getProcessName());
			eventsDto.setStatus(ServicesUtil.isEmpty(taskEventsDto.getStatus()) ? eventsDto.getStatus() : taskEventsDto.getStatus());
			eventsDto.setStatusFlag(ServicesUtil.isEmpty(taskEventsDto.getStatusFlag()) ? eventsDto.getStatusFlag() : taskEventsDto.getStatusFlag());
			eventsDto.setSubject(ServicesUtil.isEmpty(taskEventsDto.getSubject()) ? eventsDto.getSubject() : taskEventsDto.getSubject());
			eventsDto.setTaskMode(ServicesUtil.isEmpty(taskEventsDto.getTaskMode()) ? eventsDto.getTaskMode() : taskEventsDto.getTaskMode());
			eventsDto.setTaskType(ServicesUtil.isEmpty(taskEventsDto.getTaskType()) ? eventsDto.getTaskType() : taskEventsDto.getTaskType());
			eventsDto.setUrl(ServicesUtil.isEmpty(taskEventsDto.getUrl()) ? eventsDto.getUrl() : taskEventsDto.getUrl());
			eventsDto.setEventId(ServicesUtil.isEmpty(taskEventsDto.getEventId()) ? eventsDto.getEventId() : taskEventsDto.getEventId());
			eventsDto.setProcessId(ServicesUtil.isEmpty(taskEventsDto.getProcessId()) ? eventsDto.getProcessId() : taskEventsDto.getProcessId());
		
		} else {
			throw new NoResultFault("noRecordFound"
					// + "" + eventsDto.getClass().getSimpleName() + "#"
					+ " " + eventId);
		}
		return eventsDto;
	}
	
	
	
	/**
	 * 
	 * Operations over Task Owners Table
	 */
	
	
	public ResponseDto insertIntoTaskOwners(TaskOwnersDto taskOwnersDto){
		ResponseDto responseDto = new ResponseDto();
		int result = 0;
		
		String insertQuery = "INSERT INTO TASK_OWNERS(IS_PROCESSED, TASK_OWNER_EMAIL, TASK_OWNER_DISP, EVENT_ID, TASK_OWNER) VALUES ('"+((taskOwnersDto.getIsProcessed())? '1' : '0' )+"', '"+taskOwnersDto.getTaskOwnerEmail()+"', '"+taskOwnersDto.getTaskOwnerDisplayName()+"', '"+taskOwnersDto.getEventId()+"', '"+taskOwnersDto.getTaskOwner()+"')";
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][insertIntoTaskOwners][query] : "+insertQuery);
		
		try {
			result = con.insert(insertQuery);
		} catch (ExecutionFault e) {
			responseDto.setMessage("Insertion Un - Successful with Exception: "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		if(result == 1){
			responseDto.setMessage("Insertion Successful");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} else {
			responseDto.setMessage("Insertion Un - Successful");
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	public ResponseDto updateTaskOwners(TaskOwnersDto taskOwnersDto) throws NoResultFault{
		ResponseDto responseDto = new ResponseDto();
		
		int result = 0;
		
		TaskOwnersDto ownersDto = new TaskOwnersDto();
		try{
			ownersDto = this.mergeTaskOwnersDto(taskOwnersDto, taskOwnersDto.getEventId(), taskOwnersDto.getTaskOwner());
			String updateQuery = "UPDATE TASK_OWNERS SET IS_PROCESSED = '"+((ownersDto.getIsProcessed())? '1' : '0' )+"', TASK_OWNER_EMAIL = '"+ownersDto.getTaskOwnerEmail()+"', TASK_OWNER_DISP = '"+ownersDto.getTaskOwnerDisplayName()+"', EVENT_ID = '"+ownersDto.getEventId()+"', TASK_OWNER = '"+ownersDto.getTaskOwner()+"'";
			System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][updateTaskOwners][query] : "+updateQuery);
			
			result = con.update(updateQuery);
			if(result == 1){
				responseDto.setMessage("Updation Successful");
				responseDto.setStatus("SUCCESS");
				responseDto.setStatusCode("0");
			} else {
				responseDto.setMessage("Updation Un - Successful");
				responseDto.setStatus("FAILURE");
				responseDto.setStatusCode("1");
			}
		} /*catch (NoResultFault e){
			
//			insertIntoTaskOwners(taskOwnersDto);
			responseDto.setMessage("No Record Found");
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} */catch (ExecutionFault e) {
			responseDto.setMessage("Updation Un - Successful Exception : "+e.getMessage());
			responseDto.setStatus("FAILURE");
			responseDto.setStatusCode("1");
		}
		return responseDto;
	}
	
	private TaskOwnersDto mergeTaskOwnersDto(TaskOwnersDto taskOwnersDto, String eventId, String taskOwner) throws ExecutionFault, NoResultFault {
		TaskOwnersDto ownersDto = new TaskOwnersDto();
		ownersDto = this.readTaskOwners(eventId, taskOwner);
		if(!ServicesUtil.isEmpty(ownersDto)){
			ownersDto.setEventId(ServicesUtil.isEmpty(taskOwnersDto.getEventId()) ? ownersDto.getEventId() : taskOwnersDto.getEventId());
			ownersDto.setTaskOwner(ServicesUtil.isEmpty(taskOwnersDto.getTaskOwner()) ? ownersDto.getTaskOwner() : taskOwnersDto.getTaskOwner());
			ownersDto.setTaskOwnerDisplayName(ServicesUtil.isEmpty(taskOwnersDto.getTaskOwnerDisplayName()) ? ownersDto.getTaskOwnerDisplayName() : taskOwnersDto.getTaskOwnerDisplayName());
			ownersDto.setIsProcessed(ServicesUtil.isEmpty(taskOwnersDto.getIsProcessed()) ? ownersDto.getIsProcessed() : taskOwnersDto.getIsProcessed());
			ownersDto.setTaskOwnerEmail(ServicesUtil.isEmpty(taskOwnersDto.getTaskOwnerEmail()) ? ownersDto.getTaskOwnerEmail() : taskOwnersDto.getTaskOwnerEmail());
		} else {
			throw new NoResultFault("noRecordFound"
					//	+ "" + eventsDto.getClass().getSimpleName() + "#"
								+ " " +eventId + " "+taskOwner);
		}
		return ownersDto;
	}

	public TaskOwnersDto readTaskOwners(String taskId, String taskOwner) throws ExecutionFault, NoResultFault {
		TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
		ResultSet resultSet = null;
		if(!ServicesUtil.isEmpty(taskId) && !ServicesUtil.isEmpty(taskOwner)){
			String selectTaskOwner = "SELECT * FROM TASK_EVENTS WHERE EVENT_ID = '"+taskId.trim()+"' AND TASK_OWNER = '"+taskOwner.trim()+"'";
			try {
				resultSet = con.query(selectTaskOwner);
				taskOwnersDto = this.getTaskOwnersDtoFromResultSet(resultSet);
			} catch (SQLException e) {
				MessageUIDto faultInfo = new MessageUIDto();
				faultInfo.setMessage(e.getMessage());
				String message = "Retrieve of " + taskOwnersDto.getClass().getSimpleName() + " by keys " + taskId
						+ " failed!";
				throw new ExecutionFault(message, faultInfo, e);
			}
			if(ServicesUtil.isEmpty(resultSet)){
				throw new NoResultFault("noRecordFound" + taskOwnersDto.getClass().getSimpleName() + "#" +taskId);
			}
		}
		return taskOwnersDto;
	}

	private TaskOwnersDto getTaskOwnersDtoFromResultSet(ResultSet resultSet) {
		System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][getTaskOwnersDtoFromResultSet] entered with resultset : "+resultSet);
		TaskOwnersDto ownersDto = null;
		if(!ServicesUtil.isEmpty(resultSet)){
			ownersDto = new TaskOwnersDto();
			try{
				while(resultSet.next()){
					ownersDto.setEventId(resultSet.getString("EVENT_ID"));
					ownersDto.setTaskOwner(resultSet.getString("TASK_OWNER"));
					ownersDto.setTaskOwnerDisplayName(resultSet.getString("TASK_OWNER_DISP"));
					ownersDto.setTaskOwnerEmail(resultSet.getString("TASK_OWNER_EMAIL"));
					ownersDto.setIsProcessed(resultSet.getBoolean("IS_PROCESSED"));
				}
			} catch (SQLException e){
				System.err.println("[PMC][POADAPTER][HANA][HanaDBDao][getTaskOwnersDtoFromResultSet][Exception] : "+e.getMessage());
			}
		}
		return ownersDto;
	}
}