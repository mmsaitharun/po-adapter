package oneapp.workbox.poadapter.services.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.incture.pmc.poadapter.dto.ResponseDto;
import com.incture.pmc.poadapter.dto.SubstitutionRuleDto;
import com.incture.pmc.poadapter.dto.UserDto;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.tm.api.SubstitutionMode;
import com.sap.bpm.tm.api.SubstitutionRule;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * BPM API EJB.Mainly used to take work box actions using taskInstance Id.
 * 
 * @version 1.0
 * @since 2017-05-09
 */

@WebService(name = "SubstitutionManagementFacade", portName = "SubstitutionManagementFacadePort", serviceName = "SubstitutionManagementFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class SubstitutionManagementFacade implements SubstitutionManagementFacadeLocal {
	/**
	 * Default constructor.
	 */

	public SubstitutionManagementFacade() {
	}

	@WebMethod(operationName = "createRule", exclude = false)
	@Override
	public ResponseDto createRule(@WebParam(name = "ruleDto") SubstitutionRuleDto ruleDto) {
		ResponseDto response = new ResponseDto();
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][createRule] invoked" );
		try {
			SubstitutionRule rule;
			IUser substitutingUser =  UMFactory.getUserFactory().getUserByUniqueName(ruleDto.getSubstitutingUser());
			IUser substitutedUser =  UMFactory.getUserFactory().getUserByUniqueName(ruleDto.getSubstitutedUser());
			if(!ServicesUtil.isEmpty(ruleDto.getSubstitutionProfileId())){
				URI substitutionProfileId = new URI(ruleDto.getSubstitutionProfileId());
				rule = BPMFactory.getSubstitutionRuleManager().createRule(substitutingUser, substitutedUser, SubstitutionMode.valueOf(ruleDto.getMode()),ruleDto.getStartDate(),  ruleDto.getEndDate(), ruleDto.isEnabled(), substitutionProfileId);
			} else {
				rule = BPMFactory.getSubstitutionRuleManager().createRule(substitutingUser, substitutedUser, SubstitutionMode.valueOf(ruleDto.getMode()),ruleDto.getStartDate(),  ruleDto.getEndDate(), ruleDto.isEnabled());
			}
			BPMFactory.getSubstitutionRuleManager().storeRule(rule);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][createRule][response]"+rule);
			response.setMessage("Rule Successfully created with id "+rule.getSubstitutionRuleId().toString());
			response.setStatus("SUCCESS");
			response.setStatusCode("0");
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][createRule][error]" + e.getMessage());
			response.setMessage("Failed to create rule because "+e.getMessage());
			response.setStatus("FAILURE");
			response.setStatusCode("1");
		}
		return response;
	}

	@WebMethod(operationName = "getSubstitutedUsers", exclude = false)
	@Override
	public List<UserDto> getSubstitutedUsers( @WebParam(name = "substitutingUserString") String substitutingUserString) {
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substitutingUser =  UMFactory.getUserFactory().getUserByUniqueName(substitutingUserString);
			IUser[] userList = BPMFactory.getSubstitutionRuleManager().getSubstitutedUsers(substitutingUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+userList.length);

			if(!ServicesUtil.isEmpty(userList)){
				List<UserDto> userDtoList = new ArrayList<UserDto>();
				for(IUser user : userList){
					UserDto dto = new UserDto() ; 
					dto.setLoginId(user.getUniqueName());
					userDtoList.add(dto);
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+userDtoList);
				return userDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;
	}

	@WebMethod(operationName = "getSubstituteUsers", exclude = false)
	@Override
	public List<UserDto> getSubstituteUsers( @WebParam(name = "substituteUserString") String substituteUserString) {
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substituteUser =  UMFactory.getUserFactory().getUserByUniqueName(substituteUserString);
			IUser[] userList = BPMFactory.getSubstitutionRuleManager().getSubstituteUsers(substituteUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+userList.length);

			if(!ServicesUtil.isEmpty(userList)){
				List<UserDto> userDtoList = new ArrayList<UserDto>();
				for(IUser user : userList){
					UserDto dto = new UserDto() ; 
					dto.setLoginId(user.getUniqueName());
					userDtoList.add(dto);
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+userDtoList);
				return userDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;
	}

	@WebMethod(operationName = "getActiveRulesBySubstitute", exclude = false)
	@Override
	public List<SubstitutionRuleDto> getActiveRulesBySubstitute(@WebParam(name = "substitutingUser") String substitutingUser){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substitutingIUser =  UMFactory.getUserFactory().getUserByUniqueName(substitutingUser);
			SubstitutionRule[] ruleList = BPMFactory.getSubstitutionRuleManager().getActiveRulesBySubstitute(substitutingIUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+ruleList.length);

			if(!ServicesUtil.isEmpty(ruleList)){
				List<SubstitutionRuleDto> ruleDtoList = new ArrayList<SubstitutionRuleDto>();
				for(SubstitutionRule user : ruleList){
					ruleDtoList.add(convertRuleToDto(user));
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+ruleDtoList);
				return ruleDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;
	}



	@WebMethod(operationName = "getActiveRulesBySubstitutedUser", exclude = false)
	@Override
	public List<SubstitutionRuleDto> getActiveRulesBySubstitutedUser(@WebParam(name = "substitutedUser") String substitutedUser){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substitutedIUser =  UMFactory.getUserFactory().getUserByUniqueName(substitutedUser);
			SubstitutionRule[] ruleList = BPMFactory.getSubstitutionRuleManager().getActiveRulesBySubstitutedUser(substitutedIUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+ruleList.length);

			if(!ServicesUtil.isEmpty(ruleList)){
				List<SubstitutionRuleDto> ruleDtoList = new ArrayList<SubstitutionRuleDto>();
				for(SubstitutionRule user : ruleList){
					ruleDtoList.add(convertRuleToDto(user));
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+ruleDtoList);
				return ruleDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;

	}

	@WebMethod(operationName = "getInactiveRulesBySubstitute", exclude = false)
	@Override
	public List<SubstitutionRuleDto> getInactiveRulesBySubstitute(@WebParam(name = "substitutingUser") String substitutingUser){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substitutingIUser =  UMFactory.getUserFactory().getUserByUniqueName(substitutingUser);
			SubstitutionRule[] ruleList = BPMFactory.getSubstitutionRuleManager().getInactiveRulesBySubstitute(substitutingIUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+ruleList.length);

			if(!ServicesUtil.isEmpty(ruleList)){
				List<SubstitutionRuleDto> ruleDtoList = new ArrayList<SubstitutionRuleDto>();
				for(SubstitutionRule user : ruleList){
					ruleDtoList.add(convertRuleToDto(user));
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+ruleDtoList);
				return ruleDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;

	}

	@WebMethod(operationName = "getInactiveRulesBySubstitutedUser", exclude = false)
	@Override
	public List<SubstitutionRuleDto> getInactiveRulesBySubstitutedUser(@WebParam(name = "substitutedUser") String substitutedUser){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers] invoked" );
		try {
			IUser substitutedIUser =  UMFactory.getUserFactory().getUserByUniqueName(substitutedUser);
			SubstitutionRule[] ruleList = BPMFactory.getSubstitutionRuleManager().getInactiveRulesBySubstitutedUser(substitutedIUser);
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][listSize]"+ruleList.length);

			if(!ServicesUtil.isEmpty(ruleList)){
				List<SubstitutionRuleDto> ruleDtoList = new ArrayList<SubstitutionRuleDto>();
				for(SubstitutionRule user : ruleList){
					ruleDtoList.add(convertRuleToDto(user));
				}

				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][userDtoList]"+ruleDtoList);
				return ruleDtoList;
			}
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutedUsers][error]" + e.getMessage());
		}
		return null;

	}

	private SubstitutionRuleDto convertRuleToDto(SubstitutionRule rule){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][convertRuleToDto] invoked" );
		SubstitutionRuleDto dto = new SubstitutionRuleDto();
		dto.setRuleId(rule.getSubstitutionRuleId().toString());
		dto.setStartDate(rule.getStartDate());
		dto.setActive(rule.isActive());
		dto.setEnabled(rule.isEnabled());
		dto.setEndDate(rule.getEndDate());
		dto.setMode(rule.getMode().toString());
		dto.setSubstitutedUser(rule.getSubstitutedUser().getUniqueName());
		dto.setSubstitutingUser(rule.getSubstituteUser().getUniqueName());
		dto.setTakenOver(rule.isTakenOver());
		//StringBuffer substitutedUserName = new StringBuffer();
		String substitutedUserName ="",substitutingUserName="";
		substitutedUserName= rule.getSubstitutedUser().getFirstName() == null ? substitutedUserName +""
				: substitutedUserName+ rule.getSubstitutedUser().getFirstName()+ " ";
		substitutedUserName = rule.getSubstitutedUser().getLastName() == null ? substitutedUserName + "" : substitutedUserName+ rule.getSubstitutedUser().getLastName();
		substitutingUserName= rule.getSubstituteUser().getFirstName() == null ? substitutingUserName +""
				: substitutingUserName+ rule.getSubstituteUser().getFirstName()+ " ";
		substitutingUserName = rule.getSubstituteUser().getLastName() == null ? substitutingUserName + "" : substitutingUserName+ rule.getSubstituteUser().getLastName();
		
		dto.setSubstitutedUserName(substitutedUserName);
		dto.setSubstitutingUserName(substitutingUserName);
		if(!ServicesUtil.isEmpty(rule.getSubstitutionProfile())){
			System.out.println("[PMC][POAdapter][SubstitutionManagementFacade][convertRuleToDto][profileId] :"+rule.getSubstitutionProfile().getId().toString());
			dto.setSubstitutionProfileId(rule.getSubstitutionProfile().getId().toString());
		}
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][convertRuleToDto][exit] with"+dto );
		return dto;
	}
	private SubstitutionRule getSubstitutionRule(String ruleId,String user){
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutionRule] invoked[ruleId]"+ruleId+"[user]"+user );
		IUser iUser;
		try {
			iUser = UMFactory.getUserFactory().getUserByUniqueName(user);
			SubstitutionRule[] list = BPMFactory.getSubstitutionRuleManager().getRulesBySubstitutedUser(iUser);
			if(!ServicesUtil.isEmpty(list)){
				for(SubstitutionRule rule : list){
					if(rule.getSubstitutionRuleId().toString().equals(ruleId)){
						return rule;
					}
				}
			}
			list = BPMFactory.getSubstitutionRuleManager().getRulesBySubstitute(iUser);
			if(!ServicesUtil.isEmpty(list)){
				for(SubstitutionRule rule : list){
					if(rule.getSubstitutionRuleId().toString().equals(ruleId)){
						return rule;
					}
				}
			}
		} catch (UMException e) {
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getSubstitutionRule][error]"+e.getMessage() );
		}
		return null;
	}

	@WebMethod(operationName = "getRulesBySubstitutedUser", exclude = false)
	public List<SubstitutionRuleDto> getRulesBySubstitutedUser(@WebParam(name = "user") String user){
		try {
			IUser iUser = UMFactory.getUserFactory().getUserByUniqueName(user);
			SubstitutionRule[] list = BPMFactory.getSubstitutionRuleManager().getRulesBySubstitutedUser(iUser);
			List<SubstitutionRuleDto> dtoList = new ArrayList<SubstitutionRuleDto>();
			if(!ServicesUtil.isEmpty(list)){
				for(SubstitutionRule rule : list){
					dtoList.add( convertRuleToDto(rule));
				}
				return dtoList;
			}
		}catch (UMException e) {
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getRulesBySubstitute][error]"+e.getMessage() );
		}
		return null;
	}

	@WebMethod(operationName = "getRulesBySubstitute", exclude = false)
	public List<SubstitutionRuleDto> getRulesBySubstitute(@WebParam(name = "user") String user){
		try {
			IUser iUser = UMFactory.getUserFactory().getUserByUniqueName(user);
			SubstitutionRule[] list = BPMFactory.getSubstitutionRuleManager().getRulesBySubstitute(iUser);
			List<SubstitutionRuleDto> dtoList = new ArrayList<SubstitutionRuleDto>();
			if(!ServicesUtil.isEmpty(list)){
				for(SubstitutionRule rule : list){
					dtoList.add( convertRuleToDto(rule));
				}
				return dtoList;
			}
		}catch (UMException e) {
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][getRulesBySubstitute][error]"+e.getMessage() );
		}
		return null;
	}

	@WebMethod(operationName = "updateRule", exclude = false)
	public ResponseDto updateRule(@WebParam(name = "ruleDto") SubstitutionRuleDto ruleDto) {
		ResponseDto response = new ResponseDto();
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][updateRule] invoked" );
		try {
			if(!ServicesUtil.isEmpty(ruleDto)){
			SubstitutionRule rule = getSubstitutionRule(ruleDto.getRuleId(),ruleDto.getSubstitutedUser());
			if(!ServicesUtil.isEmpty(rule)){	
				rule.setEnabled(ruleDto.isEnabled());
				rule.setTakenOver(ruleDto.isTakenOver());
				BPMFactory.getSubstitutionRuleManager().storeRule(rule);
				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][updateRule][response]"+rule);
				response.setMessage("Rule Successfully updated with id "+rule.getSubstitutionRuleId().toString());
				response.setStatus("SUCCESS");
			}
			else{
				response.setMessage("No rule exists with the id "+ruleDto.getRuleId());
				response.setStatus("FAILURE");
			}
			return response;
			}
			}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][updateRule][error]" + e.getMessage());
		}
		response.setMessage("Failed to update rule");
		response.setStatus("FAILURE");
		//response.setStatusCode("1");
		return response;
	}

	@WebMethod(operationName = "deleteRule", exclude = false)
	public ResponseDto deleteRule(@WebParam(name = "ruleDto") SubstitutionRuleDto ruleDto) {
		ResponseDto response = new ResponseDto();
		System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][deleteRule] invoked" );
		try {
			SubstitutionRule rule = getSubstitutionRule(ruleDto.getRuleId(),ruleDto.getSubstitutedUser());
			if(!ServicesUtil.isEmpty(rule)){			
				BPMFactory.getSubstitutionRuleManager().deleteRule(rule);
				System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][deleteRule][response]"+rule);
				response.setMessage("Rule Successfully deleted with id "+rule.getSubstitutionRuleId().toString());
			}
			else{
				response.setMessage("No rule exists with the id "+ruleDto.getRuleId());
			}
			response.setStatus("SUCCESS");
			response.setStatusCode("0");
			return response;
		}catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionManagementFacade][deleteRule][error]" + e.getMessage());
		}
		response.setMessage("Failed to delete rule");
		response.setStatus("FAILURE");
		response.setStatusCode("1");
		return response;
	}


}
