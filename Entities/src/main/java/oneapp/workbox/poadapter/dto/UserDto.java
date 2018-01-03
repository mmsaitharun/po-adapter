package oneapp.workbox.poadapter.dto;

import javax.xml.bind.annotation.XmlRootElement;

import com.incture.pmc.poadapter.util.EnOperation;
import com.incture.pmc.poadapter.util.InvalidInputFault;

/**
 * <h1>UserDto Class Implementation</h1> No validation parameters are enforced
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@XmlRootElement
public class UserDto extends BaseDto {

	private String loginId;
	private String emailId;
	private String firstName;
	private String lastName;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public Boolean getValidForUsage() {
		return Boolean.TRUE;
	}

	@Override
	public void validate(EnOperation enOperation) throws InvalidInputFault {

	}

}
