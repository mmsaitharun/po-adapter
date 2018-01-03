package oneapp.workbox.poadapter.dao;

import javax.persistence.EntityManager;
import com.incture.pmc.poadapter.dto.TaskOwnersDto;
import com.incture.pmc.poadapter.entity.TaskOwnersDo;
import com.incture.pmc.poadapter.entity.TaskOwnersDoPK;
import com.incture.pmc.poadapter.util.ExecutionFault;
import com.incture.pmc.poadapter.util.InvalidInputFault;
import com.incture.pmc.poadapter.util.NoResultFault;
import com.incture.pmc.poadapter.util.ServicesUtil;

/**
 * The <code>TaskOwnersDao</code> converts Do to Dto and vice-versa <code>Data
 * Access Objects <code>
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
public class TaskOwnersDao extends BaseDao<TaskOwnersDo, TaskOwnersDto> {

	public TaskOwnersDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	protected TaskOwnersDto exportDto(TaskOwnersDo fromDo) {
		TaskOwnersDto outputDto = new TaskOwnersDto();
		outputDto.setEventId(fromDo.getTaskOwnersDoPK().getEventId());
		outputDto.setTaskOwner(fromDo.getTaskOwnersDoPK().getTaskOwner());
		outputDto.setTaskOwnerDisplayName(fromDo.getTaskOwnerDisplayName());
		outputDto.setTaskOwnerEmail(fromDo.getTaskOwnerEmail());
		if (!ServicesUtil.isEmpty(fromDo.getIsProcessed()))
			outputDto.setIsProcessed(fromDo.getIsProcessed());
		return outputDto;
	}

	@Override
	protected TaskOwnersDo importDto(TaskOwnersDto fromDto) throws InvalidInputFault, ExecutionFault, NoResultFault {
		TaskOwnersDo outputDo = new TaskOwnersDo();
		outputDo.setTaskOwnersDoPK(new TaskOwnersDoPK());
		outputDo.getTaskOwnersDoPK().setEventId(fromDto.getEventId());
		outputDo.setTaskOwnerDisplayName(fromDto.getTaskOwnerDisplayName());
		outputDo.getTaskOwnersDoPK().setTaskOwner(fromDto.getTaskOwner());
		outputDo.setIsProcessed(fromDto.getIsProcessed());
		outputDo.setTaskOwnerEmail(fromDto.getTaskOwnerEmail());
		return outputDo;
	}
}
