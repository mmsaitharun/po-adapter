package oneapp.workbox.poadapter.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TaskOwnerDetailsDto {
	
	private String logonId;
	
	private String displayName;
	private String ownerEmail;

	public String getLogonId() {
		return logonId;
	}

	public void setLogonId(String logonId) {
		this.logonId = logonId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	
	

}
