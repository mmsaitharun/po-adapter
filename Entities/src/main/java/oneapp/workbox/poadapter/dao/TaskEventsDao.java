package oneapp.workbox.poadapter.dao;

import javax.persistence.EntityManager;

import com.incture.pmc.poadapter.dto.TaskEventsDto;
import com.incture.pmc.poadapter.entity.TaskEventsDo;
import com.incture.pmc.poadapter.entity.TaskEventsDoPK;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.InvalidInputFault;
import com.incture.pmc.poadapter.util.NoResultFault;

/**
 * The <code>TaskEventsDao</code> converts Do to Dto and vice-versa <code>Data
 * Access Objects <code>
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public class TaskEventsDao extends BaseDao<TaskEventsDo, TaskEventsDto> {

	public TaskEventsDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected TaskEventsDto exportDto(TaskEventsDo fromDo) {
		TaskEventsDto outputDto = new TaskEventsDto();
		TaskEventsDoPK pk = fromDo.getTaskEventsDoPK();
		outputDto.setEventId(pk.getEventId());
		outputDto.setProcessId(pk.getProcessId());
		outputDto.setName(fromDo.getName());
		outputDto.setSubject(fromDo.getSubject());
		outputDto.setDescription(fromDo.getDescription());
		outputDto.setStatus(fromDo.getStatus());
		outputDto.setCurrentProcessor(fromDo.getCurrentProcessor());
		outputDto.setCurrentProcessorDisplayName(fromDo.getCurrentProcessorDisplayName());
		outputDto.setPriority(fromDo.getPriority());
		outputDto.setCreatedAt(fromDo.getCreatedAt());
		outputDto.setCompletedAt(fromDo.getCompletedAt());
		outputDto.setCompletionDeadLine(fromDo.getCompletionDeadLine());
		outputDto.setProcessName(fromDo.getProcessName());
		outputDto.setStatusFlag(fromDo.getStatusFlag());
		outputDto.setTaskMode(fromDo.getTaskMode());
		outputDto.setTaskType(fromDo.getTaskType());
		outputDto.setForwardedAt(fromDo.getForwardedAt());
		outputDto.setForwardedBy(fromDo.getForwardedBy());
		outputDto.setUrl(fromDo.getUrl());
		outputDto.setOrigin(fromDo.getOrigin());
		return outputDto;
	}

	@Override
	protected TaskEventsDo importDto(TaskEventsDto fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault {
		TaskEventsDo outputDo = new TaskEventsDo();
		TaskEventsDoPK taskEventsDoPk = new TaskEventsDoPK();
		taskEventsDoPk.setEventId(fromDto.getEventId());
		taskEventsDoPk.setProcessId(fromDto.getProcessId());
		outputDo.setTaskEventsDoPK(taskEventsDoPk);
		outputDo.setName(fromDto.getName());
		outputDo.setSubject(fromDto.getSubject());
		outputDo.setDescription(fromDto.getDescription());
		outputDo.setStatus(fromDto.getStatus());
		outputDo.setCurrentProcessor(fromDto.getCurrentProcessor());
		outputDo.setCurrentProcessorDisplayName(fromDto.getCurrentProcessorDisplayName());
		outputDo.setPriority(fromDto.getPriority());
		outputDo.setCreatedAt(fromDto.getCreatedAt());
		outputDo.setCompletedAt(fromDto.getCompletedAt());
		outputDo.setCompletionDeadLine(fromDto.getCompletionDeadLine());
		outputDo.setProcessName(fromDto.getProcessName());
		outputDo.setStatusFlag(fromDto.getStatusFlag());
		outputDo.setTaskMode(fromDto.getTaskMode());
		outputDo.setTaskType(fromDto.getTaskType());
		outputDo.setForwardedAt(fromDto.getForwardedAt());
		outputDo.setForwardedBy(fromDto.getForwardedBy());
		outputDo.setUrl(fromDto.getUrl());
		outputDo.setOrigin(fromDto.getOrigin());
		return outputDo;
	}
}
