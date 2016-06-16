package at.ac.tuwien.ase2016.wm.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sensor_group")
public class SensorGroup extends UUIDBaseEntity {

    private String name;
    private SensorGroup parent;
    private Set<Sensor> sensors = new HashSet<>();

    public SensorGroup() {
        super();
    }

    public SensorGroup(String name) {
        super();
        this.name = name;
    }

    public SensorGroup(String name, SensorGroup parent) {
        super();
        this.name = name;
        this.parent = parent;
    }

    public SensorGroup(UUID id) {
        super(id);
    }

    @NotNull
    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    public SensorGroup getParent() {
        return parent;
    }

    public void setParent(SensorGroup parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "group")
    public Set<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(Set<Sensor> sensors) {
        this.sensors = sensors;
    }
}
