package oneapp.workbox.poadapter.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: TaskOwnersDo
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@Entity
@Table(name = "TASK_OWNERS")
public class TaskOwnersDo implements BaseDo, Serializable {

	private static final long serialVersionUID = 8966817427208717661L;

	@EmbeddedId
	private TaskOwnersDoPK taskOwnersDoPK;

	@Column(name = "IS_PROCESSED")
	private Boolean isProcessed;
	
	@Column(name = "TASK_OWNER_DISP", length = 100)
	private String taskOwnerDisplayName;
	
	@Column(name = "TASK_OWNER_EMAIL", length = 60)
	private String taskOwnerEmail;

	public TaskOwnersDoPK getTaskOwnersDoPK() {
		return taskOwnersDoPK;
	}

	public void setTaskOwnersDoPK(TaskOwnersDoPK taskOwnersDoPK) {
		this.taskOwnersDoPK = taskOwnersDoPK;
	}

	public Boolean getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(Boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	

	public String getTaskOwnerDisplayName() {
		return taskOwnerDisplayName;
	}

	public void setTaskOwnerDisplayName(String taskOwnerDisplayName) {
		this.taskOwnerDisplayName = taskOwnerDisplayName;
	}

	public String getTaskOwnerEmail() {
		return taskOwnerEmail;
	}

	public void setTaskOwnerEmail(String taskOwnerEmail) {
		this.taskOwnerEmail = taskOwnerEmail;
	}

	@Override
	public Object getPrimaryKey() {
		return taskOwnersDoPK;
	}

}
