package oneapp.workbox.poadapter.hana.connector;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.incture.pmc.hana.services.HanaEntityManagerProviderLocal;
//import com.incture.pmc.hana.services.HanaEntityManagerProviderLocal;
import com.incture.pmc.poadapter.dao.ProcessEventsDao;
import com.incture.pmc.poadapter.dao.TaskEventsDao;
import com.incture.pmc.poadapter.dao.TaskOwnersDao;
import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.dto.TaskOwnerDetailsDto;
import com.incture.pmc.poadapter.dto.TaskOwnersDto;
import com.incture.pmc.poadapter.entity.TaskEventsDo;
import com.incture.pmc.poadapter.entity.TaskOwnersDo;
import com.incture.pmc.poadapter.services.ProcessManagementFacadeLocal;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.InvalidInputFault;
import com.incture.pmc.poadapter.util.LogUtil;
import com.incture.pmc.poadapter.util.NoResultFault;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.incture.pmc.poadapter.util.UmeUtil;

/**
 * Session Bean implementation class HanaDBUpdateManager
 */
@Stateless
public class HanaDBUpdateManager implements HanaDBUpdateManagerLocal {

    /**
     * Default constructor. 
     */
    public HanaDBUpdateManager() {
    }
    
    @EJB
	HanaEntityManagerProviderLocal em;
    
    /*EntityManagerFactory factory = Persistence.createEntityManagerFactory("PMC_HANA_PU");
	EntityManager manager = factory.createEntityManager();*/
    
    @EJB
	private ProcessManagementFacadeLocal processLocal;
	LogUtil logger = new LogUtil(HanaDBUpdateManager.class);
	
	@Override
	public void saveAndUpdateProcessEvent(@WebParam(name = "processEventsDto") ProcessEventsDto processEventsDto) {
		logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Method Initiated with Inputs :"
				+ processEventsDto);
		if (!ServicesUtil.isEmpty(processEventsDto) && !ServicesUtil.isEmpty(processEventsDto.getProcessId())) {
			ProcessEventsDao processEventsDao = new ProcessEventsDao(em.getEntityManager());
			//ProcessEventsDao processEventsDao = new ProcessEventsDao(manager);
			try {
				processEventsDao.update(processEventsDto);
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Update Event Error :"
						+ e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Update Event Error :"
						+ e.getMessage());
			} catch (NoResultFault e) {
				logger.logDebug(
						"[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] No Record Found therefore Save Process Event");
				try {
					processEventsDao.create(processEventsDto);
				} catch (ExecutionFault e1) {
					logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Save Event Error :"
							+ e1.getMessage());
				} catch (InvalidInputFault e1) {
					logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Save Event Error :"
							+ e1.getMessage());
				} catch (NoResultFault e1) {
					logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Save Event Error :"
							+ e1.getMessage());
				}
			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Exit");
	}
	
	@Override
	public void updateProcessEvents(@WebParam(name = "processEventsDto") ProcessEventsDto processEventsDto) {
		logger.logDebug(
				"[PMC][HanaDBUpdateManager][UpdateProcess] Method Initiated with Inputs :" + processEventsDto);
		String status = processEventsDto.getStatus();
		Date completedDate = processEventsDto.getCompletedAt();
		if (!ServicesUtil.isEmpty(processEventsDto) && !ServicesUtil.isEmpty(processEventsDto.getProcessId())) {
			ProcessEventsDao processEventsDao = new ProcessEventsDao(em.getEntityManager());
			//ProcessEventsDao processEventsDao = new ProcessEventsDao(manager);
			try {
				processEventsDto = processEventsDao.getByKeys(processEventsDto);
				processEventsDto.setStatus(status);
				processEventsDto.setCompletedAt(completedDate);
				processEventsDao.update(processEventsDto);
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Update Event Error :"
						+ e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Update Event Error :"
						+ e.getMessage());
			} catch (NoResultFault e) {
				logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] No Record Found");
			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateProcessEvent] Exit");
	}
	
	@Override
	public ProcessEventsDto readProcessEvent(@WebParam(name = "processId") String processId) {
		logger.logDebug("[PMC][HanaDBUpdateManager][readProcessEvent] Method Initiated with Inputs :" + processId);
		ProcessEventsDto processEventsDto = null;
		if (!ServicesUtil.isEmpty(processId)) {
			processEventsDto = new ProcessEventsDto();
			processEventsDto.setProcessId(processId.trim());
			ProcessEventsDao processEventsDao = new ProcessEventsDao(em.getEntityManager());
			//ProcessEventsDao processEventsDao = new ProcessEventsDao(manager);
			try {
				processEventsDto = processEventsDao.getByKeys(processEventsDto);
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readProcessEvent] Error :" + e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readProcessEvent] Error :" + e.getMessage());
			} catch (NoResultFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readProcessEvent] Error :" + e.getMessage());
			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][readProcessEvent] Exit with Output :" + processEventsDto);
		return processEventsDto;
	}
	
	@Override
	//@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public void saveAndUpdateTask(@WebParam(name = "taskEventsDto") TaskEventsDto taskEventsDto, boolean isForwarded) {
		logger.logDebug(
				"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Method Initiated with Inputs :" + taskEventsDto);
		if (!ServicesUtil.isEmpty(taskEventsDto) && !ServicesUtil.isEmpty(taskEventsDto.getProcessId().trim())
				&& !ServicesUtil.isEmpty(taskEventsDto.getEventId().trim())) {
			logger.logDebug(
					"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Method Initiated with eventId , processId");
			EntityManager entityManager = em.getEntityManager();
			//EntityManager entityManager = manager;
			TaskEventsDao taskEventsDao = new TaskEventsDao(entityManager);
			TaskOwnersDao taskOwnersDao = new TaskOwnersDao(entityManager);
			try {
				logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Start Method getProcessedOwner");
				if (isForwarded) {
					TaskEventsDto taskEventsDto1 = taskEventsDao.getByKeys(taskEventsDto);
					taskEventsDto.setForwardedBy(taskEventsDto1.getCurrentProcessor());
					taskEventsDto.setForwardedAt(new Date());
				}
				if( !ServicesUtil.isEmpty(taskEventsDto.getStatusFlag()) && taskEventsDto.getStatusFlag().equals("Reject")){
				logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateTask] getStatusFlag is reject");
				if (this.checkStatus(taskEventsDto, entityManager)) {
					taskEventsDto.setStatusFlag("Approve");
				}
				}
				taskEventsDao.update(taskEventsDto);
				if (!ServicesUtil.isEmpty(taskEventsDto.getCurrentProcessor())) {
					logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Updating Current Processor :"
							+ taskEventsDto.getCurrentProcessor());
					updateProcessedOwner(taskEventsDto.getEventId(), entityManager);
					TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
					taskOwnersDto.setTaskOwner(taskEventsDto.getCurrentProcessor());
					taskOwnersDto.setTaskOwnerDisplayName(taskEventsDto.getCurrentProcessorDisplayName());
					taskOwnersDto.setEventId(taskEventsDto.getEventId());
					taskOwnersDto.setTaskOwnerEmail(UmeUtil.getEmailIdByUserId(taskEventsDto.getCurrentProcessor()));
					taskOwnersDto.setIsProcessed(true);
					try {
						taskOwnersDao.update(taskOwnersDto);
					} catch (NoResultFault e) {
						logger.logError(
								"[PMC][HanaDBUpdateManager][saveAndUpdateTask] No Record Found therefore Save Task owner:");
						updateProcessedOwner(taskEventsDto.getEventId(), entityManager);
						taskOwnersDao.create(taskOwnersDto);
					}
				} else {
					logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Release Event Triggered");
					updateProcessedOwner(taskEventsDto.getEventId(), entityManager);
				}
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Update Event ExecutionFault :"
						+ e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Update Event InvalidInputFault :"
						+ e.getMessage());
			} catch (NoResultFault e) {
				logger.logError(
						"[PMC][HanaDBUpdateManager][saveAndUpdateTask] No Record Found therefore Save Task Event:");
				try {
					taskEventsDao.create(taskEventsDto);
					updateRequestId(taskEventsDto.getProcessId(), taskEventsDto.getRequestId(), entityManager);
					if (!ServicesUtil.isEmpty(taskEventsDto.getOwners())) {
						for (TaskOwnerDetailsDto owner : taskEventsDto.getOwners()) {
							TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
							taskOwnersDto.setTaskOwner(owner.getLogonId());
							taskOwnersDto.setTaskOwnerDisplayName(owner.getDisplayName());
							taskOwnersDto.setEventId(taskEventsDto.getEventId());
							taskOwnersDto.setIsProcessed(false);
							taskOwnersDto.setTaskOwnerEmail(owner.getOwnerEmail());
							taskOwnersDao.create(taskOwnersDto);
						}
					}
				} catch (ExecutionFault e1) {
					logger.logError(
							"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Save Event Error :" + e1.getMessage());
				} catch (InvalidInputFault e1) {
					logger.logError(
							"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Save Event Error :" + e1.getMessage());
				} catch (NoResultFault e1) {
					logger.logError(
							"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Save Event Error :" + e1.getMessage());
				}
			} catch (Exception e) {
				logger.logError(
						"[PMC][HanaDBUpdateManager][saveAndUpdateTask] Update Event Exception :" + e.getMessage());
			}

			logger.logInfo("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Save Event Triggered");
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][saveAndUpdateTask] Exit");
	}
	
	@Override
	public TaskEventsDto readTaskEvent(@WebParam(name = "processId") String processId,
			@WebParam(name = "eventId") String eventId) {
		logger.logDebug("[PMC][HanaDBUpdateManager][readTaskEvent] Method Initiated with Inputs :" + processId);
		TaskEventsDto taskEventsDto = null;
		if (!ServicesUtil.isEmpty(processId.trim()) && !ServicesUtil.isEmpty(eventId.trim())) {
			TaskEventsDto persistObj = new TaskEventsDto();
			persistObj.setProcessId(processId.trim());
			persistObj.setEventId(eventId.trim());
			TaskEventsDao taskEventsDao = new TaskEventsDao(em.getEntityManager());
			//TaskEventsDao taskEventsDao = new TaskEventsDao(manager);
			try {
				taskEventsDto = taskEventsDao.getByKeys(persistObj);
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskEvent]Error :" + e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskEvent]Error :" + e.getMessage());
			} catch (NoResultFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskEvent]Error :" + e.getMessage());
			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][readProcessEvent] Exit with Output :" + taskEventsDto);
		return taskEventsDto;
	}
	
	@Override
	public TaskOwnersDto readTaskOwner(@WebParam(name = "eventId") String eventId,
			@WebParam(name = "taskOwner") String taskOwner) {
		logger.logDebug("[PMC][HanaDBUpdateManager][readTaskOwner] Method Initiated with Inputs :" + taskOwner);
		TaskOwnersDto taskOwnersDto = null;
		if (!ServicesUtil.isEmpty(eventId.trim()) && !ServicesUtil.isEmpty(taskOwner.trim())) {
			TaskOwnersDto persistObj = new TaskOwnersDto();
			persistObj.setTaskOwner(taskOwner.trim());
			persistObj.setEventId(eventId.trim());
			TaskOwnersDao taskOwnersDao = new TaskOwnersDao(em.getEntityManager());
			//TaskOwnersDao taskOwnersDao = new TaskOwnersDao(manager);
			try {
				taskOwnersDto = taskOwnersDao.getByKeys(persistObj);
			} catch (ExecutionFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskOwner] Error :" + e.getMessage());
			} catch (InvalidInputFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskOwner] Error :" + e.getMessage());
			} catch (NoResultFault e) {
				logger.logError("[PMC][HanaDBUpdateManager][readTaskOwner] Error :" + e.getMessage());
			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][readTaskOwner] Exit Initiated with Output :" + taskOwnersDto);
		return taskOwnersDto;
	}
	
	@Override
	public void createProcess(@WebParam(name = "processEventsDto") ProcessEventsDto processEventsDto) {
		saveAndUpdateProcessEvent(processEventsDto);
		List<TaskEventsDto> taskEventsDto = processEventsDto.getTaskEvents();
		for (TaskEventsDto taskEventsDto2 : taskEventsDto) {
			saveAndUpdateTask(taskEventsDto2, false);
		}
	}
	
	@Override
	//@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public void createProcesses(@WebParam(name = "processes") List<ProcessEventsDto> processes) {
		for (ProcessEventsDto processEventsDto : processes) {
			createProcess(processEventsDto);
		}
	}
	
	private boolean checkStatus(TaskEventsDto taskEventsDto, EntityManager em) {
		try {
			logger.logDebug(
					"[PMC][HanaDBUpdateManager][checkReject][checkStatus] method invoked with" + taskEventsDto);
			Query qry = em.createQuery(
					"Select p from TaskEventsDo p where p.createdAt = ( Select MAX(p.createdAt) FROM TaskEventsDo p where p.taskEventsDoPK.processId =:processId)");
			qry.setParameter("processId", taskEventsDto.getProcessId());
			logger.logDebug("[PMC][HanaDBUpdateManager][checkReject][checkStatus][qry]" + qry);
			TaskEventsDo taskEvents = (TaskEventsDo) qry.getSingleResult();
			logger.logDebug("[PMC][HanaDBUpdateManager][checkReject][checkStatus][qryResult]" + taskEvents);
			 if(!ServicesUtil.isEmpty(taskEvents) && taskEvents.getStatusFlag().equals("Reject") && !(taskEvents.getTaskMode().equals(taskEventsDto.getTaskMode()) && taskEvents.getName().equals(taskEventsDto.getName()))){
			return this.checkIsParallel(taskEvents, taskEventsDto, em);
			 }
			 else{
				logger.logDebug("[PMC][HanaDBUpdateManager][checkStatus][returnFalse] because current task is same as previous task");
				return false; 
			 }
		} catch (Exception e) {
			logger.logDebug("[PMC][HanaDBUpdateManager][checkStatus][Exception]" + e.getMessage());
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][checkStatus][End]");
		return false;

	}
	
	private void updateProcessedOwner(String taskId, EntityManager em)
			throws ExecutionFault, InvalidInputFault, NoResultFault {
		Query qry = em.createQuery(
				"Select p FROM TaskOwnersDo p where p.isProcessed =:isProcessed and  p.taskOwnersDoPK.eventId = \'"
						+ taskId + "\'");
		qry.setParameter("isProcessed", true);
		try {
			TaskOwnersDo taskOwner = (TaskOwnersDo) qry.getSingleResult();
			TaskOwnersDao taskOwnersDao = new TaskOwnersDao(em);
			TaskOwnersDto taskOwnersDto = new TaskOwnersDto();
			taskOwnersDto.setTaskOwner(taskOwner.getTaskOwnersDoPK().getTaskOwner());
			taskOwnersDto.setTaskOwnerDisplayName(taskOwner.getTaskOwnerDisplayName());
			taskOwnersDto.setEventId(taskOwner.getTaskOwnersDoPK().getEventId());
			taskOwnersDto.setIsProcessed(false);
			taskOwnersDao.update(taskOwnersDto);
		} catch (NoResultException e) {
			logger.logError(
					"[PMC][HanaDBUpdateManager][updateProcessedOwner] No Processed Owner is Existing for the Event Id : "
							+ taskId + "|| Exception Message" + e.getMessage());
		}

	}
	
	@Override
	public void updateTaskEvents(@WebParam(name = "taskInstanceId") String taskInstanceId) {
		TaskEventsDto taskEventsDto = processLocal.getTaskDetails(taskInstanceId);
		saveAndUpdateTask(taskEventsDto, false);
	}
	
	private void updateRequestId(String processId, String requestId, EntityManager em)
			throws ExecutionFault, InvalidInputFault, NoResultFault {
		ProcessEventsDao processEventsDao = new ProcessEventsDao(em);
		ProcessEventsDto dto = new ProcessEventsDto();
		dto.setProcessId(processId);
		dto = processEventsDao.getByKeys(dto);
		dto.setRequestId(requestId);
		processEventsDao.update(dto);
	}
	
	@Override
	//@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public void updateAutomatedTask(String processId, Date CompletedDate) {
		System.err.println("updatePreviousAutomatedTask :  processId  : " + processId);
		if (!ServicesUtil.isEmpty(processId)) {
			Query qry = em.getEntityManager().createQuery(
			//Query qry = manager.createQuery(
					"Update TaskEventsDo p SET p.status = \'COMPLETED\', p.completedAt =:CompletedDate where p.taskEventsDoPK.processId =:processId and p.taskType = \'Automated\' and p.status = \'STARTED\'");
			qry.setParameter("processId", processId);
			qry.setParameter("CompletedDate", CompletedDate, TemporalType.TIMESTAMP);
			int i = qry.executeUpdate();
			System.err.println("DB updated Count : " + i);
		}
	}
	
	private boolean checkIsParallel(TaskEventsDo taskEvents, TaskEventsDto taskEventsDto, EntityManager em) {
		logger.logDebug("[PMC][HanaDBUpdateManager][checkIsParallel][start]");
		logger.logDebug("[PMC][HanaDBUpdateManager][checkIsParallel][inputData] [prevTaskMode]"+ taskEvents.getTaskMode()+"[prevTaskName]"+ taskEvents.getName()+"[curTaskMode]"+ taskEventsDto.getTaskMode()+"[curTaskName]"+taskEventsDto.getName());
		if(!ServicesUtil.isEmpty(taskEvents.getTaskMode())&&!ServicesUtil.isEmpty(taskEvents.getName())&&!ServicesUtil.isEmpty(taskEventsDto.getTaskMode())&&!ServicesUtil.isEmpty(taskEventsDto.getName())){
			try {
				Query query = em.createQuery(
						"Select count(*) from SlaManagementDo s left join TaskStepsDo ts on s.slaId = ts.slaId where s.modeName=:prevTaskMode and s.taskName=:prevTaskName and ts.modeName=:curTaskMode and ts.taskName=:curTaskName");
				query.setParameter("prevTaskMode", taskEvents.getTaskMode());
				query.setParameter("prevTaskName", taskEvents.getName());
				query.setParameter("curTaskMode", taskEventsDto.getTaskMode());
				query.setParameter("curTaskName", taskEventsDto.getName());

				BigDecimal count = (BigDecimal) query.getSingleResult();
				logger.logDebug("[PMC][HanaDBUpdateManager][checkIsParallel][count]" + count);
				if (count.equals(1)) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				logger.logDebug("[PMC][HanaDBUpdateManager][checkIsParallel][Exception]" + e.getMessage());

			}
		}
		logger.logDebug("[PMC][HanaDBUpdateManager][checkIsParallel][nullValue]");
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllProcessName() {
		Query query =  em.getEntityManager().createQuery("select DISTINCT p.processName from ProcessConfigDo p");
		//Query query =  manager.createQuery("select DISTINCT p.processName from ProcessConfigDo p");
		return (List<String>) query.getResultList();
	}
}
