package at.ac.tuwien.ase2016.wm.model;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "micro_dc_sensor_event")
@AssociationOverride(name = "sensor", joinColumns = @JoinColumn(name = "sensor_id", insertable = false, updatable = false))
public class SensorStreamInsertEvent extends SensorEvent {

    private UUID sensorId;
    private Timestamp arrivalTimestamp;

    @Id
    @Type(type = "pg-uuid")
    @Column(name = "sensor_id", nullable = false)
    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public void setSensor(Sensor sensor) {
        super.setSensor(sensor);
        if (sensor != null) {
            sensorId = sensor.getId();
        } else {
            sensorId = null;
        }
    }

    // NOTE: this is just necessary when using the workaround
    @Id
    @Column(name = "arrival_timestamp", insertable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public void setArrivalTimestamp(Timestamp arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    @PrePersist
    private void onPersist() {
        if (arrivalTimestamp == null) {
            arrivalTimestamp = Timestamp.from(Instant.now());
        }
    }

}
