package oneapp.workbox.poadapter.services.services;

import java.util.List;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.ResponseDto;
import com.incture.pmc.poadapter.dto.SubstitutionRuleDto;
import com.incture.pmc.poadapter.dto.UserDto;


@Local
public interface SubstitutionManagementFacadeLocal {

	

	ResponseDto createRule(SubstitutionRuleDto rule);

	List<UserDto> getSubstitutedUsers(String substitutingUserString);

	List<SubstitutionRuleDto> getInactiveRulesBySubstitutedUser(String substitutedUser);

	List<SubstitutionRuleDto> getInactiveRulesBySubstitute(String substitutingUser);

	List<SubstitutionRuleDto> getActiveRulesBySubstitutedUser(String substitutedUser);

	List<SubstitutionRuleDto> getActiveRulesBySubstitute(String substitutingUser);

	List<UserDto> getSubstituteUsers(String substituteUserString);

//	ResponseMessage updateRule(SubstitutionRuleDto rule);
	
}
