package oneapp.workbox.poadapter.dao;

import javax.persistence.EntityManager;

import com.incture.pmc.poadapter.dto.ProcessEventsDto;
import com.incture.pmc.poadapter.entity.ProcessEventsDo;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.InvalidInputFault;
import com.incture.pmc.poadapter.util.NoResultFault;

/**
 * The <code>ProcessEventsDao</code> converts Do to Dto and vice-versa
 * <code>Data Access Objects <code>
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public class ProcessEventsDao extends BaseDao<ProcessEventsDo, ProcessEventsDto> {
	public ProcessEventsDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected ProcessEventsDo importDto(ProcessEventsDto fromDto)
			throws InvalidInputFault, ExecutionFault, NoResultFault {
		ProcessEventsDo outputDo = new ProcessEventsDo();
		outputDo.setProcessId(fromDto.getProcessId());
		outputDo.setName(fromDto.getName());
		outputDo.setStartedBy(fromDto.getStartedBy());
		outputDo.setStatus(fromDto.getStatus());
		outputDo.setSubject(fromDto.getSubject());
		outputDo.setCompletedAt(fromDto.getCompletedAt());
		outputDo.setStartedAt(fromDto.getStartedAt());
		outputDo.setRequestId(fromDto.getRequestId());
		outputDo.setStartedByDisplayName(fromDto.getStartedByDisplayName());
		return outputDo;
	}

	@Override
	protected ProcessEventsDto exportDto(ProcessEventsDo fromDo) {
		ProcessEventsDto outputDto = new ProcessEventsDto();
		outputDto.setProcessId(fromDo.getProcessId());
		outputDto.setName(fromDo.getName());
		outputDto.setStartedBy(fromDo.getStartedBy());
		outputDto.setStatus(fromDo.getStatus());
		outputDto.setSubject(fromDo.getSubject());
		outputDto.setCompletedAt(fromDo.getCompletedAt());
		outputDto.setStartedAt(fromDo.getStartedAt());
		outputDto.setRequestId(fromDo.getRequestId());
		outputDto.setStartedByDisplayName(fromDo.getStartedByDisplayName());
		return outputDto;
	}
}
