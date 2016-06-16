package at.ac.tuwien.ase2016.wm.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
public class SensorEvent implements Serializable {

    private Sensor sensor;
    private ZonedDateTime dataFrom;
    private ZonedDateTime dataTo;

    private Float consumptionMilliLiter;
    private Float temperatureCelsius;
    private Float phValue;

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

    @Column(name = "consumption_milli_liter")
    public Float getConsumptionMilliLiter() {
        return consumptionMilliLiter;
    }

    public void setConsumptionMilliLiter(Float consumptionMilliLiter) {
        this.consumptionMilliLiter = consumptionMilliLiter;
    }

    @Column(name = "temperature_celsius")
    public Float getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(Float temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    @Column(name = "ph_value")
    public Float getPhValue() {
        return phValue;
    }

    public void setPhValue(Float phValue) {
        this.phValue = phValue;
    }

    @Override
    public String toString() {
        return "SensorEvent{" +
                "dataFrom=" + dataFrom +
                ", dataTo=" + dataTo +
                ", consumptionMilliLiter=" + consumptionMilliLiter +
                ", temperatureCelsius=" + temperatureCelsius +
                ", phValue=" + phValue +
                ", sensor=" + sensor.getId() +
                '}';
    }
}
