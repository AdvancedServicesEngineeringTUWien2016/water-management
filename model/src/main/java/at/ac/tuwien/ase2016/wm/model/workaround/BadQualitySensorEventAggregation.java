package at.ac.tuwien.ase2016.wm.model.workaround;

import at.ac.tuwien.ase2016.wm.model.Sensor;
import at.ac.tuwien.ase2016.wm.model.SensorEvent;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Subselect("micro_dc_bad_quality_alert_workaround")
@AssociationOverride(name = "sensor", joinColumns = @JoinColumn(name = "sensor_id", insertable = false, updatable = false))
public class BadQualitySensorEventAggregation extends SensorEvent {

    private UUID sensorId;
    private Timestamp arrivalTimestamp;
    private Long rowNumber;

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

    @Id
    @Column(name = "arrived_timestamp", insertable = false, columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public void setArrivalTimestamp(Timestamp arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    @Column(name = "row_number", nullable = false)
    public Long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Long rowNumber) {
        this.rowNumber = rowNumber;
    }
}
