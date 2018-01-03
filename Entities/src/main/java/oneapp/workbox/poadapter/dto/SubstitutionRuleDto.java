package oneapp.workbox.poadapter.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * <h1>SubstitutionRuleDto Class Implementation</h1> No validation parameters are
 * enforced
 * 
 * @author INC00609
 * @version 1.0
 * @since 2017-11-12
 */
@XmlRootElement
public class SubstitutionRuleDto {

	private String ruleId;
	private String substitutedUser;
	private String substitutedUserName;
	private String substitutingUser;
	private String substitutingUserName;
	private String mode;
	private Date endDate;
	private Date startDate;
	private boolean isActive;
	private boolean isEnabled;
	private boolean isTakenOver;
	private String displayStatus;
	private String substitutionProfileId;

	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getSubstitutedUser() {
		return substitutedUser;
	}
	public void setSubstitutedUser(String substitutedUser) {
		this.substitutedUser = substitutedUser;
	}
	public String getSubstitutingUser() {
		return substitutingUser;
	}
	public void setSubstitutingUser(String substitutingUser) {
		this.substitutingUser = substitutingUser;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public boolean isTakenOver() {
		return isTakenOver;
	}
	public void setTakenOver(boolean isTakenOver) {
		this.isTakenOver = isTakenOver;
	}

	public String getDisplayStatus() {
		return displayStatus;
	}
	public void setDisplayStatus(String displayStatus) {
		this.displayStatus = displayStatus;
	}
	public String getSubstitutedUserName() {
		return substitutedUserName;
	}
	public void setSubstitutedUserName(String substitutedUserName) {
		this.substitutedUserName = substitutedUserName;
	}
	public String getSubstitutingUserName() {
		return substitutingUserName;
	}
	public void setSubstitutingUserName(String substitutingUserName) {
		this.substitutingUserName = substitutingUserName;
	}
	
	@Override
	public String toString() {
		return "SubstitutionRuleDto [ruleId=" + ruleId + ", substitutedUser=" + substitutedUser
				+ ", substitutedUserName=" + substitutedUserName + ", substitutingUser=" + substitutingUser
				+ ", substitutingUserName=" + substitutingUserName + ", mode=" + mode + ", endDate=" + endDate
				+ ", startDate=" + startDate + ", isActive=" + isActive + ", isEnabled=" + isEnabled + ", isTakenOver="
				+ isTakenOver + ", displayStatus=" + displayStatus + ", substitutionProfileId=" + substitutionProfileId + "]";
	}
	/**
	 * @return the profileId
	 */
	public String getSubstitutionProfileId() {
		return substitutionProfileId;
	}
	/**
	 * @param profileId the profileId to set
	 */
	public void setSubstitutionProfileId(String substitutionProfileId) {
		this.substitutionProfileId = substitutionProfileId;
	}
}
