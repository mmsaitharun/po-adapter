package oneapp.workbox.poadapter.services.services;

import java.util.List;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.ResponseDto;
import com.incture.pmc.poadapter.dto.SubstitutionProfileDto;
import com.incture.pmc.poadapter.dto.SubstitutionProfileResponse;
import com.incture.pmc.poadapter.dto.TaskModelDto;

@Local
public interface SubstitutionProfileManagerFacadeLocal {

	SubstitutionProfileResponse createSubstitutionProfile(SubstitutionProfileDto inpProfile);
	
	SubstitutionProfileResponse getAllProfiles();

	ResponseDto deleteProfile(String profileId);

	SubstitutionProfileResponse getProfileById(String profileId);

	SubstitutionProfileResponse getProfileByKey(String profileKey);

	List<TaskModelDto> getMyTaskModelIds();

	TaskModelDto getTaskModel(String modelId);

}
