package at.ac.tuwien.ase2016.wm.server.web;

import at.ac.tuwien.ase2016.wm.model.SensorAlert;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class NotificationBean implements Serializable {

    @Inject
    private CustomerBean customerBean;

    @Inject
    private AlertBean alertBean;

    private List<SensorAlert> alerts = new ArrayList<>();
    private int unreadCount = 0;

    public void checkNewAlerts() {
        alerts.addAll(alertBean.getAlerts(customerBean.getSensorIds()));
        unreadCount = alerts.size();
    }

    public void setAllRead() {
        alertBean.markRead(alerts);
        alerts.clear();
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public List<SensorAlert> getAlerts() {
        return alerts;
    }
}
