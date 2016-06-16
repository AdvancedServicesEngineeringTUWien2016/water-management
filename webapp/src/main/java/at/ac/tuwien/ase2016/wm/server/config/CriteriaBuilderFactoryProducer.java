package at.ac.tuwien.ase2016.wm.server.config;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;

@Singleton
@Startup
public class CriteriaBuilderFactoryProducer {

	@Inject
	private EntityManagerFactory entityManagerFactory;
	@Inject
	private Event<CriteriaBuilderConfiguration> configEvent;

	private CriteriaBuilderFactory criteriaBuilderFactory;

	@PostConstruct
	public void init() {
    	CriteriaBuilderConfiguration config = Criteria.getDefault();
    	configEvent.fire(config);
    	this.criteriaBuilderFactory = config.createCriteriaBuilderFactory(entityManagerFactory);
	}

    @Produces
    @ApplicationScoped
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
    	return criteriaBuilderFactory;
    }
}