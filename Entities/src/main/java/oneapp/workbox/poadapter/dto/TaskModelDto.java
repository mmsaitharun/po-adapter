package oneapp.workbox.poadapter.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <h1>ResponseDto Class Implementation</h1> No validation parameters are
 * enforced
 * 
 * @author INC00609
 * @version 1.0
 * @since 2017-14-12
 */
@XmlRootElement
public class TaskModelDto {
	
	private String taskModelId;
	private String taskModelName;
	
	public String getTaskModelId() {
		return taskModelId;
	}
	public void setTaskModelId(String taskModelId) {
		this.taskModelId = taskModelId;
	}
	public String getTaskModelName() {
		return taskModelName;
	}
	public void setTaskModelName(String taskModelName) {
		this.taskModelName = taskModelName;
	}
	@Override
	public String toString() {
		return "TaskModelDto [taskModelId=" + taskModelId + ", taskModelName=" + taskModelName + "]";
	}


}
