package oneapp.workbox.poadapter.services.services;

import javax.ejb.Local;


@Local
public interface processActionFacadeLocal {

	String cancelProcess(String processInstanceId);


}
