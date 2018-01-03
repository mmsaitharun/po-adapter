package oneapp.workbox.poadapter.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.incture.pmc.poadapter.dto.UserDto;
import com.sap.security.api.AttributeList;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IGroupSearchFilter;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleFactory;
import com.sap.security.api.IRoleSearchFilter;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.IUserSearchFilter;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.UMRuntimeException;
import com.sap.security.api.UserAccountAlreadyExistsException;
import com.sap.security.api.UserAlreadyExistsException;

public class UmeUtil {

	public static void createUser(String userName, String firstName, String lastName, String securePassword, String emailId, String mobileNo, ArrayList<String> roleList) throws UMException {
		/*
		 * TODO: By whatever means you set these variables you must check that userName and securePassword comply with the security policy settings or be prepared to catch the exception when they do
		 * not comply.
		 */

		IUserFactory userFactory = UMFactory.getUserFactory();
		IUserAccountFactory userAccountFactory = UMFactory.getUserAccountFactory();
		IUserMaint mutableUser = userFactory.newUser(userName);
		IRoleFactory roleFactory = UMFactory.getRoleFactory();

		/*
		 * Set the LastName attribute of the user. Note: The user attribute last name is mandatory for AS ABAP data source.
		 */

		mutableUser.setLastName(lastName);
		mutableUser.setFirstName(firstName);
		mutableUser.setEmail(emailId);
		mutableUser.setTelephone(mobileNo);
		// Create a new user account with userName.
		IUserAccount userAccount = userAccountFactory.newUserAccount(userName);

		// Get the initial password of the user account.
		userAccount.setPassword(securePassword);

		// Write the changes to the user store in a single
		// transaction.
		userFactory.commitUser(mutableUser, userAccount);
		for (String role : roleList) {
			IRole tempRole = roleFactory.getRoleByUniqueName(role);
			IRole iRole = roleFactory.getMutableRole(tempRole.getUniqueID());
			if (iRole != null) {
				iRole.addUserMember(mutableUser.getUniqueID());
				iRole.save();
				iRole.commit();
			}
		}

	}

	public static void createUserUmeForVendorPortal(String userName, String firstName, String lastName, String securePassword, String emailId, String mobileNo, ArrayList<String> roleList)
			throws UMException {
		/*
		 * TODO: By whatever means you set these variables you must check that userName and securePassword comply with the security policy settings or be prepared to catch the exception when they do
		 * not comply.
		 */

		IUserFactory userFactory = UMFactory.getUserFactory();
		IUserAccountFactory userAccountFactory = UMFactory.getUserAccountFactory();
		IUserMaint mutableUser = userFactory.newUser(userName);
		IRoleFactory roleFactory = UMFactory.getRoleFactory();

		/*
		 * Set the LastName attribute of the user. Note: The user attribute last name is mandatory for AS ABAP data source.
		 */

		mutableUser.setLastName(lastName);
		mutableUser.setFirstName(firstName);
		mutableUser.setEmail(emailId);
		mutableUser.setTelephone(mobileNo);
		// Create a new user account with userName.
		IUserAccount userAccount = userAccountFactory.newUserAccount(userName);

		// Get the initial password of the user account.
		userAccount.setPassword(securePassword);
		userAccount.setPasswordChangeRequired(false);
		// Write the changes to the user store in a single
		// transaction.
		userFactory.commitUser(mutableUser, userAccount);
		for (String role : roleList) {
			IRole tempRole = roleFactory.getRoleByUniqueName(role);
			IRole iRole = roleFactory.getMutableRole(tempRole.getUniqueID());
			if (iRole != null) {
				iRole.addUserMember(mutableUser.getUniqueID());
				iRole.save();
				iRole.commit();
			}
		}

	}

	public static void createUser(String userName, String firstName, String lastName, String securePassword) {
		/*
		 * TODO: By whatever means you set these variables you must check that userName and securePassword comply with the security policy settings or be prepared to catch the exception when they do
		 * not comply.
		 */

		try {

			IUserFactory userFactory = UMFactory.getUserFactory();
			IUserAccountFactory userAccountFactory = UMFactory.getUserAccountFactory();
			IUserMaint mutableUser = userFactory.newUser(userName);

			/*
			 * Set the LastName attribute of the user. Note: The user attribute last name is mandatory for AS ABAP data source.
			 */

			mutableUser.setLastName(lastName);
			mutableUser.setFirstName(firstName);

			// Create a new user account with userName.
			IUserAccount userAccount = userAccountFactory.newUserAccount(userName);

			// Get the initial password of the user account.
			userAccount.setPassword(securePassword);

			// Write the changes to the user store in a single
			// transaction.

			userFactory.commitUser(mutableUser, userAccount);

		} catch (UserAlreadyExistsException uaee) {
			// TODO: Handle UserAlreadyExistsException.
		} catch (UserAccountAlreadyExistsException uaaee) {
			// TODO: Handle UserAccountAlreadyExistsException.
		} catch (UMException umex) {
			// TODO: Handle UMException.
		} catch (UMRuntimeException umrex) {
			// TODO: Handle UMRuntimeException.
		}

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<RoleInfoDto> getAssignedRole() {
		LogUtil logger = new LogUtil(UmeUtil.class);
		ArrayList<RoleInfoDto> list = new ArrayList<RoleInfoDto>();
		try {
			IUser loggedInUser = UMFactory.getLogonAuthenticator().getLoggedInUser();
			if (loggedInUser == null) {
				throw new SecurityException("Withought loging user is trying to access role...");
			}
			Iterator<String> roles = loggedInUser.getRoles(true);//
			while (roles.hasNext()) {
				RoleInfoDto dto = new RoleInfoDto();
				String roleId = roles.next();
				IRole role = UMFactory.getRoleFactory().getRole(roleId);
				dto.setRoleDescription(role.getDescription());
				dto.setRoleUniqName(role.getDisplayName());
				list.add(dto);
			}

		} catch (Exception e) {
			logger.logError("[getAssignedRole] Error " + e.getMessage());
		}
		return list;

	}


	public static ArrayList<UserDetailsDto> getUsersByRole(String role) {
		ArrayList<UserDetailsDto> userDtoList= null;
		try {

			IRole irole = UMFactory.getRoleFactory().getRoleByUniqueName(role);
			if(!ServicesUtil.isEmpty(irole))
			{
				userDtoList = new ArrayList<UserDetailsDto>();
				String[] userList=UMFactory.getRoleFactory().getUsersOfRole(irole.getUniqueID(), true);
				for(String user:userList)
				{

					IUser iuser = UMFactory.getUserFactory().getUser(user);
					System.err.println("[PMC][PoAdapter][UmeUtil][getUsersByRole][user]"+ user +"[iuser]"+iuser.getUniqueName());
					UserDetailsDto dto = new UserDetailsDto();
					dto.setDisplayName(iuser.getDisplayName());
					dto.setFirstName(iuser.getFirstName());
					dto.setLastName(iuser.getLastName());
					//	dto.setEmailId(iuser.getEmail());
					dto.setUserId(iuser.getUniqueName());
					//	dto.setMobileNo(iuser.getCellPhone());
					userDtoList.add(dto);
					//	getUserDetailsByUserId(user)
				}
			}

		} catch (Exception e) {
			System.err.println("getAssignedRole - " + e.getMessage());
			e.printStackTrace();
		}
		return userDtoList;
	}

	/*public static String[] getRoleIdBYRoleUniqueName(String roleUniqueName) {



		IRole irole=UMFactory.getRoleFactory().getRoleByUniqueName(roleUniqueName);

		return irole;
	}*/

	@SuppressWarnings("unchecked")
	public static ArrayList<RoleInfoDto> getAssignedRole(String uniqueId) {
		LogUtil logger = new LogUtil(UmeUtil.class);
		ArrayList<RoleInfoDto> list = new ArrayList<RoleInfoDto>();
		try {
			IUser loggedInUser = UMFactory.getUserFactory().getUserByLogonID(uniqueId);
			if (loggedInUser == null) {
				throw new SecurityException("Withought loging user is trying to access role...");
			}
			System.err.println("getAssignedRole - " + loggedInUser.getUniqueID());
			Iterator<String> roles = loggedInUser.getRoles(true);
			while (roles.hasNext()) {
				RoleInfoDto dto = new RoleInfoDto();
				String roleId = roles.next();
				IRole role = UMFactory.getRoleFactory().getRole(roleId);
				dto.setRoleDescription(role.getDescription());
				dto.setRoleUniqName(role.getDisplayName());
				list.add(dto);
			}

		} catch (Exception e) {
			logger.logError("[getAssignedRole] Error " + e.getMessage());
		}
		return list;

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<GroupInfoDto> getAssignedGroup() {
		LogUtil logger = new LogUtil(UmeUtil.class);
		ArrayList<GroupInfoDto> list = new ArrayList<GroupInfoDto>();
		try {
			IUser loggedInUser = UMFactory.getLogonAuthenticator().getLoggedInUser();
			if (loggedInUser == null) {
				throw new SecurityException("Withought loging user is trying to access role...");
			}
			Iterator<String> group = loggedInUser.getParentGroups(true);

			while (group.hasNext()) {
				GroupInfoDto dto = new GroupInfoDto();
				String groupId = group.next();
				IGroup iGroup = UMFactory.getGroupFactory().getGroup(groupId);
				dto.setGroupDiscription(iGroup.getDescription());
				dto.setGroupUniqName(iGroup.getDisplayName());
				list.add(dto);
			}

		} catch (Exception e) {
			logger.logError("[getAssignedGroup] Error " + e.getMessage());
		}
		return list;

	}

	@SuppressWarnings("unchecked")
	public static ArrayList<GroupInfoDto> getAssignedGroup(String uniqueId) {
		LogUtil logger = new LogUtil(UmeUtil.class);
		ArrayList<GroupInfoDto> list = new ArrayList<GroupInfoDto>();
		try {
			IUser loggedInUser = UMFactory.getUserFactory().getUserByLogonID(uniqueId);
			if (loggedInUser == null) {
				throw new SecurityException("Withought loging user is trying to access role...");
			}
			Iterator<String> group = loggedInUser.getParentGroups(true);

			while (group.hasNext()) {
				GroupInfoDto dto = new GroupInfoDto();
				String groupId = group.next();
				IGroup iGroup = UMFactory.getGroupFactory().getGroup(groupId);
				dto.setGroupDiscription(iGroup.getDescription());
				dto.setGroupUniqName(iGroup.getDisplayName());
				list.add(dto);
			}

		} catch (Exception e) {
			logger.logError("[getAssignedGroup] Error " + e.getMessage());
		}
		return list;

	}

	public static IUser getLoggedinUser() {
		IUser loggedInUser = UMFactory.getLogonAuthenticator().getLoggedInUser();
		return loggedInUser;
	}

	public static String getLoggedinUserId() {
		IUser loggedInUser = getLoggedinUser();
		return loggedInUser.getUniqueName();
	}

	public static String getEmailIdByUserId(String userId) {
		String emailId = "";
		try {
			emailId = UMFactory.getUserFactory().getUserByLogonID(userId).getEmail();
		} catch (UMException e) {
			e.printStackTrace();
		}
		return emailId;
	}

	public static String getDisplayNameByUserId(String userId) {
		String displayName = "";
		try {
			displayName = UMFactory.getUserFactory().getUserByLogonID(userId).getDisplayName();
		} catch (UMException e) {
			e.printStackTrace();
		}
		return displayName;
	}

	public static void deleteUser(String userId) throws UMException {
		IUserFactory userFact = UMFactory.getUserFactory();
		IUser user = userFact.getUserByLogonID(userId);
		String uniqueId = user.getUniqueID();
		userFact.deleteUser(uniqueId);
	}

	public static void blockUser(String userId, boolean block) throws UMException {
		LogUtil logger = new LogUtil(UmeUtil.class);
		IUserAccount user = UMFactory.getUserAccountFactory().getUserAccountByLogonId(userId);
		IUserAccount useraccount = null;
		useraccount = UMFactory.getUserAccountFactory().getMutableUserAccount(user.getUniqueID());
		logger.logDebug("[blockUser] Unique Id: " + user.getUniqueID());
		if (block) {
			useraccount.setLocked(true, IUserAccount.LOCKED_BY_ADMIN);
		} else {
			useraccount.setLocked(block, IUserAccount.LOCKED_NO);

		}

		useraccount.commit();
	}

	public static ArrayList<String> getGroupUsers(String groupId) throws UMException {
		// LogUtil logger = new LogUtil(UmeUtil.class);
		// IGroup group = UMFactory.getGroupFactory().getGroup(groupId);
		// IUser user;
		IGroup group = UMFactory.getGroupFactory().getGroupByUniqueName(groupId);
		ArrayList<String> usersId = new ArrayList<String>();
		Iterator<?> users = group.getUserMembers(true);
		while (users.hasNext()) {
			String userid = "";
			userid = users.next().toString();
			// user=
			// UMFactory.getUserFactory().getUserByUniqueName(users.next().toString());
			usersId.add(userid);
		}
		return usersId;
	}

	public static ArrayList<String> getUsersAssignedInGroup(String groupId) throws UMException {
		IGroup group = UMFactory.getGroupFactory().getGroupByUniqueName(groupId);
		ArrayList<String> userList = new ArrayList<String>();
		Iterator<?> users = group.getUserMembers(true);
		while (users.hasNext()) {
			String userId = UMFactory.getUserFactory().getUser(users.next().toString()).getUniqueName();
			userList.add(userId);
		}
		return userList;
	}

	public static ArrayList<UserDto> getUserDetailsAssignedInGroup(String groupId) throws UMException {
		IGroup group = UMFactory.getGroupFactory().getGroupByUniqueName(groupId);
		ArrayList<UserDto> userList = new ArrayList<UserDto>();
		Iterator<?> users = group.getUserMembers(true);
		while (users.hasNext()) {
			UserDto user = new UserDto();
			IUser iuser = UMFactory.getUserFactory().getUser(users.next().toString());
			user.setLoginId(iuser.getUniqueName());
			user.setFirstName(iuser.getFirstName());
			user.setLastName(iuser.getLastName());
			userList.add(user);
		}
		return userList;
	}

	public static ArrayList<String> getGroupUsersEmailIds(String groupId) throws UMException {
		IGroup group = UMFactory.getGroupFactory().getGroupByUniqueName(groupId);
		Iterator<?> users = group.getUserMembers(true);
		ArrayList<String> usersEmailIdList = new ArrayList<String>();
		while (users.hasNext()) {
			String userId = UMFactory.getUserFactory().getUser(users.next().toString()).getUniqueName();
			System.err.println("UTIL userId " + userId);
			// IUser user = UMFactory.getUserFactory().getUserByLogonID(userId);
			// System.err.println("UTIL userId " +user);
			usersEmailIdList.add(UMFactory.getUserFactory().getUserByLogonID(userId).getEmail());
		}
		return usersEmailIdList;
	}

	public boolean isGroupExists(String groupId) throws UMException {
		boolean isExists = false;
		IGroupFactory grpFact = UMFactory.getGroupFactory();
		IGroupSearchFilter grpFilt = grpFact.getGroupSearchFilter();
		grpFilt.setUniqueName(groupId, ISearchAttribute.EQUALS_OPERATOR, false);
		ISearchResult result = grpFact.searchGroups(grpFilt);
		if (result.getState() == ISearchResult.SEARCH_RESULT_OK) {
			if (result.size() > 0)
				isExists = true;
		}
		return isExists;
	}

	public static boolean isUserExists(String userId) throws UMException {
		boolean isExists = false;
		IUserFactory usrFact = UMFactory.getUserFactory();
		IUserSearchFilter usrFilt = usrFact.getUserSearchFilter();
		usrFilt.setUniqueName(userId, ISearchAttribute.EQUALS_OPERATOR, false);
		ISearchResult result = usrFact.searchUsers(usrFilt);
		if (result.getState() == ISearchResult.SEARCH_RESULT_OK) {
			if (result.size() > 0)
				isExists = true;
		}
		return isExists;
	}

	public static UserDetailsDto getUserDetailsByUserId(String userId) {
		UserDetailsDto userDto = null;

		try {
			IUserFactory userFact = UMFactory.getUserFactory();
			IUser user = userFact.getUserByLogonID(userId);
			userDto = new UserDetailsDto();
			userDto.setEmailId(user.getEmail());
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setMobileNo(user.getTelephone());
			userDto.setUserId(user.getUniqueID());
			userDto.setDisplayName(user.getDisplayName());
			userDto.setPhoto(user.getPhoto());
		} catch (UMException e) {
			System.err.println("getUserDetailsByUserId : - " + e.getMessage());
		}
		return userDto;
	}

	/**
	 * @param logonId
	 * @param groupId
	 * @return Boolean
	 * @throws UMException
	 */
	public static boolean isMemberOfGroup(String logonId, String groupId) throws UMException {
		IUser iuser = UMFactory.getUserFactory().getUserByLogonID(logonId);
		return iuser.isMemberOfGroup(groupId, true);
	}

	/**
	 * @param logonId
	 * @param roleId
	 * @return
	 * @throws UMException
	 */
	public static boolean isMemberOfRole(String logonId, String roleId) throws UMException {
		IUser iuser = UMFactory.getUserFactory().getUserByLogonID(logonId);
		return iuser.isMemberOfGroup(roleId, true);
	}

	/**
	 * @return List<IGroup>
	 */
	public static List<IGroup> getAllUMEGroup() {
		List<IGroup> iGroupList = null;
		IGroupFactory groupFactory = UMFactory.getGroupFactory();
		if (!UMFactory.isInitialized())
			System.err.println("UME is not initialized");
		try {
			IGroupSearchFilter filter = groupFactory.getGroupSearchFilter();
			ISearchResult result = groupFactory.searchGroups(filter);
			iGroupList = new ArrayList<IGroup>();
			while (result.hasNext())
				iGroupList.add(groupFactory.getGroup(result.next().toString()));
		} catch (UMException e) {
			System.err.println("error: " + e.getLocalizedMessage());
		}
		return iGroupList;
	}

	/**
	 * @return List<IRole>
	 */

	public static List<IRole> getAllUMERole() {
		List<IRole> iRoleList = null;
		IRoleFactory roleFactory = UMFactory.getRoleFactory();
		if (!UMFactory.isInitialized())
			System.err.println("UME is not initialized");
		try {
			IRoleSearchFilter filter=roleFactory.getRoleSearchFilter();
			ISearchResult result = roleFactory.searchRoles(filter);
			iRoleList = new ArrayList<IRole>();
			while (result.hasNext())
				iRoleList.add(roleFactory.getRole(result.next().toString()));
		} catch (UMException e) {
			System.err.println("error: " + e.getLocalizedMessage());
		}
		return iRoleList;
	}





	/**
	 * @return List<String>
	 */
	public static List<String> getAllUMEGroupName() {
		List<String> groupNameList = new ArrayList<String>();
		for (IGroup group : getAllUMEGroup()) {
			groupNameList.add(group.getDisplayName());
		}
		return groupNameList;
	}

	public static List<String> getAllUMERoleName() {
		List<String> roleNameList = new ArrayList<String>();
		for(IRole role : getAllUMERole()){
			roleNameList.add(role.getDisplayName());
		}
		return roleNameList;
	}



	public static List<UserDto> getAllUsers(String userSearch) {
		List<UserDto> dtos = null;
		try {
			IUserFactory users = UMFactory.getUserFactory();
			IUserSearchFilter searchFilter;
			searchFilter = users.getUserSearchFilter();
			//searchFilter.setEmail("*" + userSearch + "*", ISearchAttribute.LIKE_OPERATOR, false);
			searchFilter.setUniqueName("*" + userSearch + "*", ISearchAttribute.LIKE_OPERATOR, false);
			// searchFilter.setFirstName("*" + userSearch + "*", ISearchAttribute.LIKE_OPERATOR, false);
			// searchFilter.setLastName("*" + userSearch + "*", ISearchAttribute.LIKE_OPERATOR, false);
			// searchFilter.setEmail("*" + userSearch + "*", ISearchAttribute.LIKE_OPERATOR, false);
			searchFilter.setMaxSearchResultSize(0);
			ISearchResult searchResults = users.searchUsers(searchFilter);
			System.err.println("searchResults size1 : " + searchResults.size());
			if ((searchResults != null) && (searchResults.size() > 0)) {
				dtos = new ArrayList<UserDto>();
				while ((searchResults.hasNext())) {
					String userUniqueId = (String) searchResults.next();
					if (userUniqueId != null) {
						AttributeList attrList = new AttributeList();
						attrList.addAttribute(IUser.DEFAULT_NAMESPACE, IUser.UNIQUE_NAME, AttributeList.TYPE_STRING);
						attrList.addAttribute(IUser.DEFAULT_NAMESPACE, IUser.DISPLAYNAME, AttributeList.TYPE_STRING);
						IUser user = users.getUser(userUniqueId, attrList);
						if ((user != null) && ((user.getName()) != null) && isUserExists(user.getName()) && !isUserBlocked(user.getName())) {
							UserDto userDto = new UserDto();
							userDto.setLoginId(user.getName());
							userDto.setFirstName(user.getFirstName());
							userDto.setEmailId(user.getEmail());
							userDto.setLastName(user.getLastName());
							dtos.add(userDto);
						}
					}
				}
			}
			return dtos;
		} catch (UMException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isUserBlocked(String userId) {
		boolean isUserBlocked = false;
		if (userId != null) {
			try {
				IUserAccount userAccount = UMFactory.getUserAccountFactory().getUserAccountByLogonId(userId.trim());
				isUserBlocked = userAccount.isUserAccountLocked();
			} catch (UMException e) {
				e.printStackTrace();
			}
		}
		return isUserBlocked;
	}

	//	public static doLogOut(){
	//		IUser loggedInUser = UMFactory.getAuthenticator().getLoggedInUser();
	//		UMFactory.getAuthenticator().logout(arg0, arg1);
	//	}

}
