package oneapp.workbox.poadapter.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.incture.pmc.poadapter.dto.UserDto;
import com.incture.pmc.poadapter.services.UMEUserManagementFacadeLocal;
import com.incture.pmc.poadapter.util.UserDetailsDto;
import com.incture.pmc.poadapter.util.UserGroupDto;

@Path("/ume")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class UMEUserRest {

	@EJB
	private UMEUserManagementFacadeLocal ume;

	@GET
	@Path("/userdetail/{userId}")
	public UserDetailsDto getUserDetailsByUserId(@PathParam("userId") String userId) {
		return ume.getUserDetailsByUserId(userId);
	}

	@GET
	@Path("/usergroups")
	public List<UserGroupDto> getAllUserGroup() {
		return ume.getAllUserGroup();
	}

	@GET
	@Path("/logon")
	public UserDetailsDto getLoggedInUser() {
		return ume.getLoggedInUser();
	}

	@GET
	@Path("/searchUser/{userId}")
	public List<UserDto> getUserBycharacter(@PathParam("userId") String searchUser) {
		return ume.getAllUsers(searchUser);
	}
}
