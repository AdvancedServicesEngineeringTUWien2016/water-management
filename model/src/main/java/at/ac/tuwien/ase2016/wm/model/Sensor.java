package at.ac.tuwien.ase2016.wm.model;

import at.ac.tuwien.ase2016.wm.model.geometry.Point;
import at.ac.tuwien.ase2016.wm.model.simulation.SensorAttribute;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "sensor")
public class Sensor extends UUIDBaseEntity {

    private Point location;
    private Integer sampleSeconds;
    private SensorGroup group;
    private Map<SensorAttribute, SensorSimulationConfiguration> simulationConfiguration = new HashMap<>();

    public Sensor() {
        super();
    }

    public Sensor(UUID id) {
        super(id);
    }

    @Column(name = "location", nullable = false, columnDefinition = "point")
    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    @Column(name = "sample_seconds", nullable = false)
    public Integer getSampleSeconds() {
        return sampleSeconds;
    }

    public void setSampleSeconds(Integer sampleSeconds) {
        this.sampleSeconds = sampleSeconds;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_group_id", nullable = true)
    public SensorGroup getGroup() {
        return group;
    }

    public void setGroup(SensorGroup group) {
        this.group = group;
    }

    @ElementCollection
    @CollectionTable(name = "sensor_simulation_configuration")
    @MapKeyEnumerated(EnumType.STRING)
    public Map<SensorAttribute, SensorSimulationConfiguration> getSimulationConfiguration() {
        return simulationConfiguration;
    }

    public void setSimulationConfiguration(Map<SensorAttribute, SensorSimulationConfiguration> simulationConfiguration) {
        this.simulationConfiguration = simulationConfiguration;
    }
}
