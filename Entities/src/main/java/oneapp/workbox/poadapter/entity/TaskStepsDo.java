package oneapp.workbox.poadapter.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: TaskStepsDo
 *
 */
@Entity
@Table(name = "TASK_STEP_MAPPING")
public class TaskStepsDo implements BaseDo, Serializable {

	private static final long serialVersionUID = 1L;

	public TaskStepsDo() {
		super();
	}

	@Id
	@Column(name = "TASK_MAPPING_ID", length = 50)
	private String taskMappingId;

	@Column(name = "TASK_SLA_ID", length = 50)
	private String taskSlaId;

	@Column(name = "NEXT_TASK_ID", length = 100)
	private String nestSlaId;

	public String getTaskMappingId() {
		return taskMappingId;
	}

	public void setTaskMappingId(String taskMappingId) {
		this.taskMappingId = taskMappingId;
	}

	public String getTaskSlaId() {
		return taskSlaId;
	}

	public void setTaskSlaId(String taskSlaId) {
		this.taskSlaId = taskSlaId;
	}

	public String getNestSlaId() {
		return nestSlaId;
	}

	public void setNestSlaId(String nestSlaId) {
		this.nestSlaId = nestSlaId;
	}

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
