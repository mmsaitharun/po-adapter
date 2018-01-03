package oneapp.workbox.poadapter.dto;

import java.util.List;

public class SubstitutionProfileDto {

	private String profileKey;
	private String profileName;
	private String profileId;
	private List<TaskModelDto> taskModelIds;

	public String getProfileKey() {
		return profileKey;
	}

	public void setProfileKey(String profileKey) {
		this.profileKey = profileKey;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public List<TaskModelDto> getTaskModelIds() {
		return taskModelIds;
	}

	public void setTaskModelIds(List<TaskModelDto> taskModelIds) {
		this.taskModelIds = taskModelIds;
	}

	@Override
	public String toString() {
		return "SubstitutionProfileDto [profileKey=" + profileKey + ", profileName=" + profileName + ", profileId="
				+ profileId + ", taskModelIds=" + taskModelIds + "]";
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

}
