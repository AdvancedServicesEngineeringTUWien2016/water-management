package at.ac.tuwien.ase2016.wm.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "customer")
public class Customer extends UUIDBaseEntity {

    private String name;
    private Set<SensorGroup> sensorGroups = new HashSet<>();

    public Customer() {
        super();
    }

    public Customer(UUID id) {
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

    @ManyToMany
    @JoinTable(name = "customer_sensor_groups")
    public Set<SensorGroup> getSensorGroups() {
        return sensorGroups;
    }

    public void setSensorGroups(Set<SensorGroup> sensorGroups) {
        this.sensorGroups = sensorGroups;
    }
}
