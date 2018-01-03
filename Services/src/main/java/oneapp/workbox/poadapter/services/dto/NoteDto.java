package oneapp.workbox.poadapter.services.dto;

import java.util.Date;

public class NoteDto {

	private String content;
	private Date createdOn;
	private String id;
	private String createdBy;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	@Override
	public String toString() {
		return "NoteDto [content=" + content + ", createdOn=" + createdOn + ", id=" + id + ", createdBy=" + createdBy
				+ "]";
	}
	
	
	
}
