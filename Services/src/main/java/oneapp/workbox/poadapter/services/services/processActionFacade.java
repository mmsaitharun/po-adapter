package oneapp.workbox.poadapter.services.services;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Stateless;

import com.incture.pmc.poadapter.util.ServicesUtil;
import com.sap.bpm.api.BPMFactory;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * BPM API EJB.Mainly used to take work box actions using taskInstance Id.
 * 
 * @version 1.0
 * @since 2017-05-09
 */

@WebService(name = "processActionFacade", portName = "processActionFacadePort", serviceName = "processActionFacadeService", targetNamespace = "http://incture.com/pmc/poadapter/services/")
@Stateless
public class processActionFacade implements processActionFacadeLocal {
	/**
	 * Default constructor.
	 */

	public processActionFacade() {
	}
	@WebMethod(operationName = "cancelProcess", exclude = false)
	@Override
	public String cancelProcess(@WebParam(name = "processInstanceId") String processInstanceId) {
		System.err.println("[PMC][POAdapterServices][processActionFacade][cancelProcess] method invoked" );
		System.err.println("[PMC][POAdapterServices][processActionFacade][cancelProcess][instanceId]" + processInstanceId);
		if(!ServicesUtil.isEmpty(processInstanceId)){
		URI processInstance;
		try {
			processInstance = new URI(processInstanceId);
			BPMFactory.getProcessInstanceManager().cancel(processInstance);
			return "SUCCESS";
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.err.println("[PMC][POAdapterServices][WorkBoxFacade][cancelProcess][errorMessage] "+e.getMessage() );
			return "Failed because " + e.getMessage();
		}
		}
		else{
			return "Failed because no processInstance Id in the input";
		}
	}


}
