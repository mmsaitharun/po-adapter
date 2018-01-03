package oneapp.workbox.poadapter.services.events;

import java.net.URI;
import java.util.UUID;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import com.incture.pmc.hana.connector.HanaDBConnector;
import com.incture.pmc.hana.connector.HanaDBDao;
import com.incture.pmc.hana.connector.HanaDBUpdateManagerLocal;
import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.services.DatabaseManagementFacadeLocal;
import com.incture.pmc.poadapter.services.ProcessManagementFacadeWSDLConsumerLocal;
import com.incture.pmc.poadapter.util.LogUtil;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.sap.bpm.jms.api.AbstractBPMMessageListener;
import com.sap.bpm.jms.api.BPMEventType;
import com.sap.bpm.jms.api.BPMProcessEventMessage;
import com.sap.bpm.jms.api.BPMTaskEventMessage;

/**
 * MDB Implementation to consume SAP BPM Events. ProcessManagementFacadeWSDLConsumerLocal dependency injected for getting all the details using eventId. DatabaseManagementFacadeLocal dependency
 * injected for save/update events in DB.
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") }, mappedName = AbstractBPMMessageListener.JMS_TOPIC_NAME)
public class ConsumeBPMEventsBean extends AbstractBPMMessageListener {

	LogUtil logger = new LogUtil(ConsumeBPMEventsBean.class);

	@EJB
	private ProcessManagementFacadeWSDLConsumerLocal processLocal;

	@EJB
	private DatabaseManagementFacadeLocal dbLocal;
	
	/*@EJB
	private HanaDBUpdateManagerLocal hanaDbLocal;*/

	@Override
	public void onBPMProcessEvent(BPMProcessEventMessage bpmProcessEventMessage) {
		logger.logDebug("[PMC][ConsumeBPMEventsBean][onBPMProcessEvent] Method Initiated with Input||Process Event Name :" + bpmProcessEventMessage.getBPMEventType().name() + "Event Time Stamp :"
				+ bpmProcessEventMessage.getEventTimestamp().getTime());
		HanaDBDao hanaDao = new HanaDBDao(HanaDBConnector.getInstance());
		if (BPMEventType.PROCESS_STARTED.name().equalsIgnoreCase(bpmProcessEventMessage.getBPMEventType().name())) {
			URI processInstanceId = bpmProcessEventMessage.getProcessInstanceId();
			logger.logDebug("[PMC][ConsumeBPMEventsBean][onBPMProcessEvent] process Started with requestId : " + processLocal.getProcessDetails(processInstanceId.toString()).getRequestId());
			ProcessEventsDto processEventsDto = new ProcessEventsDto();
			processEventsDto = processLocal.getProcessDetails(processInstanceId.toString());
			//dbLocal.saveAndUpdateProcessEvent(processLocal.getProcessDetails(processInstanceId.toString()));
			dbLocal.saveAndUpdateProcessEvent(processEventsDto);
			//hanaDbLocal.saveAndUpdateProcessEvent(processEventsDto);
			hanaDao.updateProcessEvents(processEventsDto);
		} else if (BPMEventType.PROCESS_COMPLETED.name().equalsIgnoreCase(bpmProcessEventMessage.getBPMEventType().name())) {
			System.err.println("Completed");
			ProcessEventsDto processEventsDto = new ProcessEventsDto();
			processEventsDto.setProcessId(bpmProcessEventMessage.getProcessInstanceId().toString());
			processEventsDto.setStatus("COMPLETED");
			processEventsDto.setCompletedAt(bpmProcessEventMessage.getEventTimestamp());
			try {
				System.err.println("Thread sleep");
				Thread.sleep(1000);
				System.err.println("Thread awake");
			} catch (InterruptedException e) {
				System.err.println("Exception in thread while sleeping");
				e.printStackTrace();
			}
			dbLocal.updateAutomatedTask(bpmProcessEventMessage.getProcessInstanceId().toString(), bpmProcessEventMessage.getEventTimestamp());
//			hanaDbLocal.updateAutomatedTask(bpmProcessEventMessage.getProcessInstanceId().toString(), bpmProcessEventMessage.getEventTimestamp());
			dbLocal.updateProcessEvents(processEventsDto);
			//hanaDbLocal.updateProcessEvents(processEventsDto);
			hanaDao.updateProcessEvents(processEventsDto);
		} else if (BPMEventType.PROCESS_CANCELED.name().equalsIgnoreCase(bpmProcessEventMessage.getBPMEventType().name())) {
			System.err.println("Cancelled");
			ProcessEventsDto processEventsDto = new ProcessEventsDto();
			processEventsDto.setProcessId(bpmProcessEventMessage.getProcessInstanceId().toString());
			processEventsDto.setStatus("CANCELLED");
			processEventsDto.setCompletedAt(bpmProcessEventMessage.getEventTimestamp());
			dbLocal.updateAutomatedTask(bpmProcessEventMessage.getProcessInstanceId().toString(), bpmProcessEventMessage.getEventTimestamp());
//			hanaDbLocal.updateAutomatedTask(bpmProcessEventMessage.getProcessInstanceId().toString(), bpmProcessEventMessage.getEventTimestamp());
			dbLocal.updateProcessEvents(processEventsDto);
			//hanaDbLocal.updateProcessEvents(processEventsDto);
			hanaDao.updateProcessEvents(processEventsDto);
		} else if (BPMEventType.SERVICETASK_STARTED.name().equalsIgnoreCase(bpmProcessEventMessage.getBPMEventType().name())) {
			System.err.println("Automated Task");
			URI processInstanceId = bpmProcessEventMessage.getProcessInstanceId();
			ProcessEventsDto process = processLocal.getProcessDetails(processInstanceId.toString());
			if (!process.isAutomatedIgnored()) {
				dbLocal.updateAutomatedTask(processInstanceId.toString(), bpmProcessEventMessage.getEventTimestamp());
//				hanaDbLocal.updateAutomatedTask(processInstanceId.toString(), bpmProcessEventMessage.getEventTimestamp());
				ProcessEventsDto dto = null;
				TaskEventsDto taskEventsDto = null;
				if (!ServicesUtil.isEmpty(processInstanceId))
					dto = processLocal.getProcessDetails(processInstanceId.toString());
				System.err.println("Auotomated Dto : " + dto);
				if (!ServicesUtil.isEmpty(dto)) {
					taskEventsDto = new TaskEventsDto();
					taskEventsDto.setProcessId(dto.getProcessId());
					taskEventsDto.setCreatedAt(bpmProcessEventMessage.getEventTimestamp());
					taskEventsDto.setEventId(UUID.randomUUID().toString());
					taskEventsDto.setName(dto.getName());
					taskEventsDto.setProcessName(dto.getName());
					taskEventsDto.setSubject(dto.getSubject());
					taskEventsDto.setStatus("STARTED");
					taskEventsDto.setTaskType("Automated");
					taskEventsDto.setOrigin("BPM");
					System.err.println("saveAndUpdateTask - Automated Start");
					dbLocal.saveAndUpdateTask(taskEventsDto, false);
//					hanaDbLocal.saveAndUpdateTask(taskEventsDto, false);
					hanaDao.saveAndUpdateTask(taskEventsDto, false);
				}
				System.err.println("saveAndUpdateTask - Automated End");
			}
		}
		logger.logDebug("[PMC][ConsumeBPMEventsBean][onBPMProcessEvent] Exit" + bpmProcessEventMessage.getBPMEventType().name());
	}

	@Override
	public void onBPMTaskEvent(BPMTaskEventMessage bpmTaskEventMessage) {
		HanaDBDao hanaDao = new HanaDBDao(HanaDBConnector.getInstance());
		logger.logDebug("[PMC][ConsumeBPMEventsBean][onBPMProcessEvent] Method Initiated with Input||Task Event Name :" + bpmTaskEventMessage.getBPMEventType().name() + "Event Time Stamp :"
				+ bpmTaskEventMessage.getEventTimestamp().getTime());
		if (BPMEventType.USERTASK_ACTIVATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_CLAIMED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_COMPLETED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_CANCELED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_DELEGATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_RELEASED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
				|| BPMEventType.USERTASK_NOMINATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())) {
			TaskEventsDto taskEventsDto = null;
			taskEventsDto = processLocal.getTaskDetails(bpmTaskEventMessage.getTaskInstanceId().toString());
			// dbLocal.updateAutomatedTask(taskEventsDto.getProcessId(), taskEventsDto.getCreatedAt());
			boolean isForwarded = false;
			if (BPMEventType.USERTASK_DELEGATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())
					|| BPMEventType.USERTASK_NOMINATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name()))
				isForwarded = true;
			dbLocal.saveAndUpdateTask(taskEventsDto, isForwarded);
			
			hanaDao.saveAndUpdateTask(taskEventsDto, isForwarded);
//			hanaDbLocal.saveAndUpdateTask(taskEventsDto, isForwarded);
		}
		if (BPMEventType.USERTASK_CREATED.name().equalsIgnoreCase(bpmTaskEventMessage.getBPMEventType().name())) {
			TaskEventsDto taskEventsDto = null;
			taskEventsDto = processLocal.getTaskDetails(bpmTaskEventMessage.getTaskInstanceId().toString());
			dbLocal.updateAutomatedTask(taskEventsDto.getProcessId(), taskEventsDto.getCreatedAt());
//			hanaDbLocal.updateAutomatedTask(taskEventsDto.getProcessId(), taskEventsDto.getCreatedAt());
		}
		logger.logDebug("[PMC][ConsumeBPMEventsBean][onBPMTaskEvent] Exit" + bpmTaskEventMessage.getBPMEventType().name());
	}
}
