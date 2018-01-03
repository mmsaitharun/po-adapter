package oneapp.workbox.poadapter.services;

import javax.ejb.Local;
import javax.persistence.EntityManager;

@Local
public interface EntityManagerProviderLocal {

	public EntityManager getEntityManager();

	String sayHello();

}