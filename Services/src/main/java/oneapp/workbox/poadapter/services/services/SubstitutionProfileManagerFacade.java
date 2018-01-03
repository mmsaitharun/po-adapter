package oneapp.workbox.poadapter.services.services;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import com.incture.pmc.poadapter.dto.ResponseDto;
import com.incture.pmc.poadapter.dto.SubstitutionProfileDto;
import com.incture.pmc.poadapter.dto.SubstitutionProfileResponse;
import com.incture.pmc.poadapter.dto.TaskModelDto;
import com.incture.pmc.poadapter.util.ServicesUtil;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.tm.api.SubstitutionProfile;
import com.sap.bpm.tm.api.TaskModel;
import javax.jws.WebParam;

/**
 * Session Bean implementation class SubstitutionProfileManagementFacade
 */
@WebService(name = "SubstitutionProfileManagerFacade", portName = "SubstitutionProfileManagerFacadePort", serviceName = "SubstitutionProfileManagerFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class SubstitutionProfileManagerFacade implements SubstitutionProfileManagerFacadeLocal {

	/**
	 * Default constructor. 
	 */
	public SubstitutionProfileManagerFacade() {
	}

	@WebMethod(operationName = "createSubstitutionProfile", exclude = false)
	@Override
	public SubstitutionProfileResponse createSubstitutionProfile(@WebParam(name = "profileDto") SubstitutionProfileDto profileDto){
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][createSubstitutionProfile] inititated with : "+profileDto);
		SubstitutionProfileResponse returnMessage = new SubstitutionProfileResponse();
		ResponseDto response = new ResponseDto();
		URI taskModelId;
		if(!ServicesUtil.isEmpty(profileDto.getTaskModelIds())  && !ServicesUtil.isEmpty(profileDto.getProfileName()) ){
			try{
				Random rn = new Random();
				profileDto.setProfileKey(((new BigInteger("100000000000")).add( new BigInteger(Long.toString(rn.nextInt())).mod(new BigInteger("900000000000")))).toString());
				List<URI> taskModelIds = new ArrayList<URI>();
				for(int i = 0; i<profileDto.getTaskModelIds().size(); i++){
					taskModelId = new URI(profileDto.getTaskModelIds().get(i).getTaskModelId());
					taskModelIds.add(taskModelId);
				}
				SubstitutionProfile profile = BPMFactory.getSubstitutionProfileManager().createProfile(profileDto.getProfileKey(), profileDto.getProfileName(), taskModelIds.toArray(new URI[taskModelIds.size()]));
				BPMFactory.getSubstitutionProfileManager().storeProfile(profile);
				System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][createSubstitutionProfile] values :"+profile.getKey()+" Name: "+profile.getDefaultName());
				List<SubstitutionProfileDto> dtoList = new ArrayList<SubstitutionProfileDto>();
				dtoList.add(convertProfileToDto(profile));
				returnMessage.setProfiles(dtoList);

				response.setMessage("Profile Successfully created with id "+profile.getId().toString());
				response.setStatus("SUCCESS");
				response.setStatusCode("0");
			} catch (Exception e){
				System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][createSubstitutionProfile][error]" + e.getMessage());
				response.setMessage("Failed to create profile because "+e.getMessage());
				response.setStatus("FAILURE");
				response.setStatusCode("1");
			}
		} else {
			System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][createSubstitutionProfile][error] : mandatory fields not found");
			response.setMessage("Please fill the mandatory fields");
			response.setStatus("FAILURE");
			response.setStatusCode("1");
		}
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][createSubstitutionProfile] ended with : "+profileDto);
		returnMessage.setResponseMessage(response);
		return returnMessage;
	}

	@WebMethod(operationName = "getAllProfiles", exclude = false)
	@Override
	public SubstitutionProfileResponse getAllProfiles(){
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getAllProfiles] inititated");
		SubstitutionProfileResponse response = null;
		ResponseDto respMessage = new ResponseDto();
		List<SubstitutionProfileDto> responseDtos = null;
		List<SubstitutionProfile> profiles = BPMFactory.getSubstitutionProfileManager().getAllProfiles();
		if(!ServicesUtil.isEmpty(profiles)){
			response = new SubstitutionProfileResponse();
			responseDtos = new ArrayList<SubstitutionProfileDto>();
			for(SubstitutionProfile profile : profiles){
				responseDtos.add(convertProfileToDto(profile));
			}
			response.setProfiles(responseDtos);
			respMessage.setMessage("Substitution Profiles Fetched");
		} else {
			response = new SubstitutionProfileResponse();
			response.setProfiles(null);
			respMessage.setMessage("Substitution Profiles Empty");
		}
		respMessage.setStatus("SUCCESS");
		respMessage.setStatusCode("0");
		response.setResponseMessage(respMessage);
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getAllProfiles] ended with : "+responseDtos);
		return response;
	}

	@WebMethod(operationName = "deleteProfile", exclude = false)
	@Override
	public ResponseDto deleteProfile(@WebParam(name = "profileId") String profileId){
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][deleteProfile] inititated with : "+profileId);
		ResponseDto response = new ResponseDto();
		try{
			URI substitutionProfileId = new URI(profileId);
			BPMFactory.getSubstitutionProfileManager().deleteProfile(substitutionProfileId);
			response.setMessage("Profile Successfully deleted with id "+profileId);
			response.setStatus("SUCCESS");
			response.setStatusCode("0");
		} catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][deleteProfile][error]" + e.getMessage());
			response.setMessage("Exception : "+e.getLocalizedMessage());
			response.setStatus("FAILURE");
			response.setStatusCode("1");
		}
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][deleteProfile] ended with : "+profileId);
		return response;
	}

	@WebMethod(operationName = "getProfileById", exclude = false)
	@Override
	public SubstitutionProfileResponse getProfileById(@WebParam(name = "profileId") String profileId){
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileById] inititated with : "+profileId);
		SubstitutionProfileResponse response = null;
		List<SubstitutionProfileDto> profilesList = null;
		ResponseDto respMessage = new ResponseDto();
		try{
			response = new SubstitutionProfileResponse();
			profilesList = new ArrayList<SubstitutionProfileDto>();
			URI substitutionProfileId = new URI(profileId);
			SubstitutionProfile profile = BPMFactory.getSubstitutionProfileManager().getProfileById(substitutionProfileId);
			if(!ServicesUtil.isEmpty(profile)){
				profilesList.add(convertProfileToDto(profile));
				response.setProfiles(profilesList);
				respMessage.setMessage("Substitution Profiles Fetched");
			} else {
				response.setProfiles(null);
				respMessage.setMessage("Substitution Profiles Empty");
			}
			respMessage.setStatus("SUCCESS");
			respMessage.setStatusCode("0");
			response.setResponseMessage(respMessage);

		} catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileById][error]" + e.getLocalizedMessage());
			response.setProfiles(null);
			respMessage.setMessage("Exception : "+e.getLocalizedMessage());
			respMessage.setStatus("FAILURE");
			respMessage.setStatusCode("1");
			response.setResponseMessage(respMessage);
		}
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileById] ended with : "+response);
		return response;
	}

	@WebMethod(operationName = "getProfileByKey", exclude = false)
	@Override
	public SubstitutionProfileResponse getProfileByKey(@WebParam(name = "profileKey") String profileKey){
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileByKey] inititated with : "+profileKey);
		SubstitutionProfileResponse response = null;
		ResponseDto respMessage = new ResponseDto();
		List<SubstitutionProfileDto> profilesList = null;
		try{
			response = new SubstitutionProfileResponse();
			profilesList = new ArrayList<SubstitutionProfileDto>();
			SubstitutionProfile profile = BPMFactory.getSubstitutionProfileManager().getProfileByKey(profileKey);
			if(!ServicesUtil.isEmpty(profile)){
				profilesList.add(convertProfileToDto(profile));
				response.setProfiles(profilesList);
				respMessage.setMessage("Substitution Profiles Fetched");
			} else {
				response.setProfiles(null);
				respMessage.setMessage("Substitution Profiles Empty");
			}
			respMessage.setStatus("SUCCESS");
			respMessage.setStatusCode("0");
			response.setResponseMessage(respMessage);
		} catch (Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileByKey][error]" + e.getMessage());
			response.setProfiles(null);
			respMessage.setMessage("Exception : "+e.getLocalizedMessage());
			respMessage.setStatus("FAILURE");
			respMessage.setStatusCode("1");
			response.setResponseMessage(respMessage);
		}
		System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getProfileByKey] ended with : "+response);
		return response;
	}


	private SubstitutionProfileDto convertProfileToDto(SubstitutionProfile profile){

		SubstitutionProfileDto dto = new SubstitutionProfileDto();

		dto.setProfileName(profile.getDefaultName());
		dto.setProfileKey(profile.getKey());
		dto.setProfileId(profile.getId().toString());
		List<TaskModelDto> taskModelIds = new ArrayList<TaskModelDto>();
		for(URI uri : profile.getTaskModelIds()){
			taskModelIds.add(getTaskModel(uri.toString()));
		}
		dto.setTaskModelIds(taskModelIds);
		return dto;
	}

	private TaskModelDto convertModelToDto(TaskModel taskModel){
		if(!ServicesUtil.isEmpty(taskModel)){
			TaskModelDto modelDto = new TaskModelDto();
			modelDto.setTaskModelId(taskModel.getId().toString());
			modelDto.setTaskModelName(taskModel.getName());
			return modelDto;
		}
		return null;	
	}


	@WebMethod(operationName = "getMyTaskModelIds", exclude = false)
	@Override
	public List<TaskModelDto> getMyTaskModelIds(){
		List<TaskModelDto> taskModelIds = null;
		Set<com.sap.bpm.tm.api.Status> statuses = new HashSet<com.sap.bpm.tm.api.Status>();
		statuses.add(com.sap.bpm.tm.api.Status.CREATED);
		statuses.add(com.sap.bpm.tm.api.Status.READY);
		statuses.add(com.sap.bpm.tm.api.Status.RESERVED);
		statuses.add(com.sap.bpm.tm.api.Status.IN_PROGRESS);
		statuses.add(com.sap.bpm.tm.api.Status.COMPLETED);
		List<TaskModel> taskModels = BPMFactory.getTaskModelManager().getMyTaskmodels(statuses);
		if(!ServicesUtil.isEmpty(taskModels)){
			taskModelIds = new ArrayList<TaskModelDto>();
			for(TaskModel taskModel : taskModels){
				taskModelIds.add(convertModelToDto(taskModel));
			}
		}
		return taskModelIds;
	}


	@WebMethod(operationName = "getTaskModel", exclude = false)
	@Override
	public TaskModelDto getTaskModel(String modelId){
		try{
			return convertModelToDto(BPMFactory.getTaskModelManager().getTaskModel( new URI(modelId)));
		} catch(Exception e){
			System.err.println("[PMC][POAdapter][SubstitutionProfileManagerFacade][getTaskModel][error]" + e.getMessage());
		}
		return null;
	}

}
