package at.ac.tuwien.ase2016.wm.service.api.alert;

import java.io.Serializable;
import java.util.UUID;

public class AlertEvent implements Serializable {

    private final UUID alertId;

    AlertEvent(){
        alertId = null;
    }

    public AlertEvent(UUID alertId) {
        this.alertId = alertId;
    }

    public UUID getAlertId() {
        return alertId;
    }
}
