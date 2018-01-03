package oneapp.workbox.poadapter.services.services;

import java.net.URI;

import javax.ejb.Stateless;

import com.incture.pmc.poadapter.dto.CustomAttributeDto;
import com.sap.bpm.api.BPMFactory;
import com.sap.bpm.tm.api.TaskAbstract;
import com.sap.bpm.tm.api.TaskAbstractCustomAttributesCriteria;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * Session Bean implementation class CustomAttributesService
 */
@WebService(name = "CustomAttributesService", portName = "CustomAttributesServicePort", serviceName = "CustomAttributesServiceService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class CustomAttributesService implements CustomAttributesServiceLocal {

  @WebMethod(operationName = "getCustomAttributes", exclude = false)
  	@Override
	public CustomAttributeDto getCustomAttributes(@WebParam(name = "taskId") String taskId){
		CustomAttributeDto dto = new CustomAttributeDto();
		URI taskUri = URI.create(taskId);
		TaskAbstractCustomAttributesCriteria ca = new TaskAbstractCustomAttributesCriteria();
		TaskAbstract taskAbstarct=BPMFactory.getTaskInstanceManager().getTaskAbstract(taskUri, ca);
		dto.setCustomAttribute(taskAbstarct.getCustomAttributeValues());
		return dto;
	}

}
