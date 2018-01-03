
package oneapp.workbox.poadapter.services.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.incture.pmc.poadapter.dto.UserDto;
import com.incture.pmc.poadapter.util.GroupInfoDto;
import com.incture.pmc.poadapter.util.RoleInfoDto;
import com.incture.pmc.poadapter.util.UmeUtil;
import com.incture.pmc.poadapter.util.UserDetailsDto;
import com.incture.pmc.poadapter.util.UserGroupDto;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;

/**
 * SAP PO UME API EJB.Mainly used to get all user related information.
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@WebService(name = "UMEUserManagementFacade", portName = "UMEUserManagementFacadePort", serviceName = "UMEUserManagementFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class UMEUserManagementFacade implements UMEUserManagementFacadeLocal {

	public UMEUserManagementFacade() {
	}

	@WebMethod(operationName = "getUserDetailsByUserId", exclude = false)
	@Override
	public UserDetailsDto getUserDetailsByUserId(@WebParam(name = "userId") String userId) {
		return UmeUtil.getUserDetailsByUserId(userId);
	}

	@WebMethod(operationName = "getAllUserGroup", exclude = false)
	@Override
	public List<UserGroupDto> getAllUserGroup() {
		List<UserGroupDto> userGroupDtos = new ArrayList<UserGroupDto>();
		List<String> userGroupNames = UmeUtil.getAllUMEGroupName();
		for (String groupName : userGroupNames) {
			UserGroupDto dto = new UserGroupDto();
			dto.setGroupName(groupName);
			userGroupDtos.add(dto);
		}
		UserGroupDto allUserGroupDto = new UserGroupDto();
		allUserGroupDto.setGroupName("ALL");
		userGroupDtos.add(allUserGroupDto);
		return userGroupDtos;
	}

	@WebMethod(operationName = "getUsersAssignedInGroup", exclude = false)
	@Override
	public List<String> getUsersAssignedInGroup(@WebParam(name = "userGroup") String userGroup) {
		List<String> users = null;
		try {
			users = UmeUtil.getUsersAssignedInGroup(userGroup);
		} catch (UMException e) {
		}
		return users;
	}

	@WebMethod(operationName = "getUserGroupByuserId", exclude = false)
	@Override
	public List<GroupInfoDto> getUserGroupByuserId(@WebParam(name = "userUniqueId") String userUniqueId) {
		return UmeUtil.getAssignedGroup(userUniqueId);
	}

	@WebMethod(operationName = "getUserRoleByuserId", exclude = false)
	@Override
	public ArrayList<RoleInfoDto> getUserRoleByuserId(@WebParam(name = "userUniqueId") String userUniqueId) {
		return UmeUtil.getAssignedRole(userUniqueId);
	}
	
	@WebMethod(operationName = "getUserEmailByuserId", exclude = false)
	@Override
	public String getUserEmailByuserId(@WebParam(name = "userUniqueId") String userUniqueId) {
		return UmeUtil.getEmailIdByUserId(userUniqueId);
		//return "inc.pmc.test@gmail.com";
	}
	
	@WebMethod(operationName = "getLoggedInUser", exclude = false)
	@Override
	public UserDetailsDto getLoggedInUser() {
		UserDetailsDto userInformation = new UserDetailsDto();
		IUser user = UmeUtil.getLoggedinUser();
		userInformation.setUserId(user.getUniqueName());
		StringBuffer name = new StringBuffer();
		name = user.getFirstName() == null ? name.append("")
				: name.append(user.getFirstName()).append(" ");
		name = user.getLastName() == null ? name.append("")
				: name.append(user.getLastName());
		userInformation.setDisplayName(name.toString().trim());
		userInformation.setEmailId(user.getEmail());
		userInformation.setMobileNo(user.getCellPhone());
		userInformation.setPhoto(user.getPhoto());
		return userInformation;
	}
	
	@WebMethod(operationName = "getAllUsers", exclude = false)
	@Override
	public List<UserDto> getAllUsers(@WebParam(name = "userSearch") String userSearch) {
		return UmeUtil.getAllUsers(userSearch);
	}
	
	@WebMethod(operationName = "getAllUserRole", exclude = false)
	@Override
	public List<RoleInfoDto> getAllUserRole() {
		List<RoleInfoDto> userRoleDtos = new ArrayList<RoleInfoDto>();
		List<String> userRoleNames = UmeUtil.getAllUMERoleName();
		for (String roleName : userRoleNames) {
			RoleInfoDto dto = new RoleInfoDto();
			dto.setRoleUniqName(roleName);
			userRoleDtos.add(dto);
		}
		RoleInfoDto allUserRoleDto = new RoleInfoDto();
		allUserRoleDto.setRoleUniqName("ALL");
		userRoleDtos.add(allUserRoleDto);
		return userRoleDtos;
	}
	
	@WebMethod(operationName = "getUserDetailsAssignedInGroup", exclude = false)
	@Override
	public List<UserDto> getUserDetailsAssignedInGroup(@WebParam(name = "getUserDetailsAssignedInGroup") String groupName) {
		try {
			return UmeUtil.getUserDetailsAssignedInGroup(groupName);
		} catch (UMException e) {
			// TODO Auto-generated catch block
			System.err.println("[PMC][POAdapter][UMEUserManagementFacade][getUserDetailsAssignedInGroup][error]"+e.getMessage());
		}
		return null;
	}
	
	@WebMethod(operationName = "getUsersByRole", exclude = false)
	public ArrayList<UserDetailsDto> getUsersByRole(@WebParam(name = "role") String role)
	{
		return UmeUtil.getUsersByRole(role);
	}

}
