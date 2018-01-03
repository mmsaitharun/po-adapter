package oneapp.workbox.poadapter.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
public class EntityManagerProvider implements EntityManagerProviderLocal {

	@PersistenceContext(unitName = "PMC_PU")
	private EntityManager em;

	public EntityManagerProvider() {
	}

	@Override
	public String sayHello() {
		Query q = em.createQuery("Select p From ProcessEventsDo p ");
		q.getResultList();
		return "Hi....";
	}

	@Override
	public EntityManager getEntityManager() {
		return em;
	}
}