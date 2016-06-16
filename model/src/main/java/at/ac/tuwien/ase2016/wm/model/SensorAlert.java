package at.ac.tuwien.ase2016.wm.model;

import at.ac.tuwien.ase2016.wm.model.simulation.SensorAttribute;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sensor_alert")
public class SensorAlert extends UUIDBaseEntity {

    private static final DateTimeFormatter format = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    private ZonedDateTime createdAt;
    private AlertType type;
    private Sensor sensor;
    private ZonedDateTime dataFrom;
    private ZonedDateTime dataTo;
    private Boolean read = false;

    public SensorAlert() {
        super();
    }

    public SensorAlert(UUID id) {
        super(id);
    }

    @Transient
    public String getText() {
        return "Alert '" + type.name() + "' in the time from " + format.format(dataFrom) + " to " + format.format(dataTo);
    }

    @Transient
    public Date getCreatedAtDate() {
        return Date.from(dataFrom.toInstant());
    }

    @Column(name = "created_at", nullable = false)
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @NotNull
    @Column(name = "data_from", nullable = false)
    public ZonedDateTime getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(ZonedDateTime dataFrom) {
        this.dataFrom = dataFrom;
    }

    @NotNull
    @Column(name = "data_to", nullable = false)
    public ZonedDateTime getDataTo() {
        return dataTo;
    }

    public void setDataTo(ZonedDateTime dataTo) {
        this.dataTo = dataTo;
    }

    @Column(name = "read", nullable = false)
    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
