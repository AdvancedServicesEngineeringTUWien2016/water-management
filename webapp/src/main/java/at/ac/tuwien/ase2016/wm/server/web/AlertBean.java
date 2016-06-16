package at.ac.tuwien.ase2016.wm.server.web;

import at.ac.tuwien.ase2016.wm.model.SensorAlert;
import at.ac.tuwien.ase2016.wm.service.api.alert.AlertEvent;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Stateless
public class AlertBean {

    private final Map<UUID, UUID> alerts = new ConcurrentHashMap<>();

    @Inject
    private EntityManager em;

    public void onAlert(@Observes AlertEvent event) {
        alerts.put(event.getAlertId(), event.getAlertId());
    }

    public List<SensorAlert> getAlerts(List<UUID> sensorIds) {
        List<SensorAlert> alerts = em.createQuery("SELECT a FROM SensorAlert a WHERE a.read = false AND a.sensor.id IN (:sensorIds)", SensorAlert.class)
                .setParameter("sensorIds", sensorIds)
                .getResultList();

        return alerts.stream().filter(a -> this.alerts.remove(a.getId()) != null).collect(Collectors.toList());
    }

    public void markRead(List<SensorAlert> alertList) {
        em.createQuery("UPDATE SensorAlert a SET a.read = true WHERE a.id IN(:alertIds)")
                .setParameter("alertIds", alertList.stream().map(a -> a.getId()).collect(Collectors.toList()))
                .executeUpdate();
    }
}
