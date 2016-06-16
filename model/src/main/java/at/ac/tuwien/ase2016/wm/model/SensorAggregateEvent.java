package at.ac.tuwien.ase2016.wm.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensor_aggregate_event")
@AssociationOverride(name = "sensor", joinColumns = @JoinColumn(name = "sensor_id", insertable = false, updatable = false))
public class SensorAggregateEvent extends SensorEvent {

    private UUID sensorId;
    private ZonedDateTime periodBucket;

    private Long elementCount;
    private Float temperatureCelsiusSum;
    private Float phValueSum;

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
    @Column(name = "period_timestamp", nullable = false)
    public ZonedDateTime getPeriodBucket() {
        return periodBucket;
    }

    public void setPeriodBucket(ZonedDateTime periodBucket) {
        this.periodBucket = periodBucket;
    }

    @Column(name = "element_count", nullable = false)
    public Long getElementCount() {
        return elementCount;
    }

    public void setElementCount(Long elementCount) {
        this.elementCount = elementCount;
    }

    @Column(name = "temperature_celsius_sum", nullable = false)
    public Float getTemperatureCelsiusSum() {
        return temperatureCelsiusSum;
    }

    public void setTemperatureCelsiusSum(Float temperatureCelsiusSum) {
        this.temperatureCelsiusSum = temperatureCelsiusSum;
    }

    @Column(name = "ph_value_sum", nullable = false)
    public Float getPhValueSum() {
        return phValueSum;
    }

    public void setPhValueSum(Float phValueSum) {
        this.phValueSum = phValueSum;
    }
}
