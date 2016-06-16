package at.ac.tuwien.ase2016.wm.server.web;

import at.ac.tuwien.ase2016.wm.model.SensorAggregateEvent;
import com.blazebit.persistence.CriteriaBuilderFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CustomerDataAccess {

    @Inject
    private EntityManager em;
    @Inject
    private CriteriaBuilderFactory cbf;

    public List<UUID> getSensors() {
        // TODO: right now we assume a single customer
        return em.createQuery("SELECT s.id FROM Customer c LEFT JOIN c.sensorGroups sensorGroup LEFT JOIN sensorGroup.sensors s", UUID.class).getResultList();
    }

    public List<SensorAggregateEvent> getSensorData(List<UUID> sensorIds) {
        // TODO: maybe we need a timeframe
        return em.createQuery("SELECT e FROM SensorAggregateEvent e WHERE e.sensorId IN (:sensorIds) ORDER BY e.sensorId, e.periodBucket")
                .setParameter("sensorIds", sensorIds)
                .getResultList();
    }

}
