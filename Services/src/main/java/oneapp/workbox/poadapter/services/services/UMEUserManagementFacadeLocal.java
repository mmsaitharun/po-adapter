package oneapp.workbox.poadapter.services.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;

import com.incture.pmc.poadapter.dto.UserDto;
import com.incture.pmc.poadapter.util.GroupInfoDto;
import com.incture.pmc.poadapter.util.RoleInfoDto;
import com.incture.pmc.poadapter.util.UserDetailsDto;
import com.incture.pmc.poadapter.util.UserGroupDto;

@Local
public interface UMEUserManagementFacadeLocal {

	public UserDetailsDto getUserDetailsByUserId(String userId);

	public List<UserGroupDto> getAllUserGroup();

	public List<String> getUsersAssignedInGroup(String userGroup);

	public List<GroupInfoDto> getUserGroupByuserId(String userUniqueId);

	public ArrayList<RoleInfoDto> getUserRoleByuserId(String userUniqueId);

	UserDetailsDto getLoggedInUser();

	List<UserDto> getAllUsers(String userSearch);

	String getUserEmailByuserId(String userUniqueId);

	List<RoleInfoDto> getAllUserRole();

	List<UserDto> getUserDetailsAssignedInGroup(String groupName);

	ArrayList<UserDetailsDto> getUsersByRole(String role);
}
