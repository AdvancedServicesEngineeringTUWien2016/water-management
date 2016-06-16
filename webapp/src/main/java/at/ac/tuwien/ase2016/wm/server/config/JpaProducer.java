package at.ac.tuwien.ase2016.wm.server.config;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

public class JpaProducer {

	@Produces
	@PersistenceContext(unitName = "ASE2016-WM")
	private EntityManager em;

	@Produces
	@PersistenceUnit(unitName = "ASE2016-WM")
	private EntityManagerFactory emf;
}
