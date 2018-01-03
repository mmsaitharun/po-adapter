package oneapp.workbox.poadapter.dto;

import java.util.List;

public class SubstitutionProfileResponse {
	List<SubstitutionProfileDto> profiles;
	ResponseDto responseMessage;

	public List<SubstitutionProfileDto> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<SubstitutionProfileDto> profiles) {
		this.profiles = profiles;
	}

	public ResponseDto getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ResponseDto responseMessage) {
		this.responseMessage = responseMessage;
	}

	@Override
	public String toString() {
		return "SubstitutionProfileResponse [profiles=" + profiles + ", responseMessage=" + responseMessage + "]";
	}

}
