package oneapp.workbox.poadapter.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Mainly a wrapper over EntityManager. Intended as an equivalent of
 * EntityManagerFactory, since Dependency Injection is more prominent in J2EE
 * world. Instance can also be passed as an argument within/across non bean
 * classes. Also includes some utility methods written over Query API's.
 * 
 * @author INC00400
 * @version 1.0
 * @since 2017-05-09
 */
@Stateless
public class HanaEntityManagerProvider implements HanaEntityManagerProviderLocal {

	@PersistenceContext(unitName = "PMC_HANA_PU")
	private EntityManager em;

	public HanaEntityManagerProvider() {
	}

	/*@Override
	public String sayHello() {
		Query q = em.createQuery("Select p From ProcessEventsDo p ");
		q.getResultList();
		return "Hi....";
	}*/

	@Override
	public EntityManager getEntityManager() {
		/*EntityManagerFactory factory = Persistence.createEntityManagerFactory("PMC_HANA_PU");
		EntityManager manager = factory.createEntityManager();
		return manager;*/
		return em;
	}
}