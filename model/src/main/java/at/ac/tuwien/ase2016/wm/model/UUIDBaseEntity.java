package at.ac.tuwien.ase2016.wm.model;

import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public class UUIDBaseEntity extends BaseEntity<UUID> {

    public UUIDBaseEntity() {
        this(UUID.randomUUID());
    }

    public UUIDBaseEntity(UUID id) {
        super(id);
    }

    @Id
    @Override
    @Type(type = "pg-uuid")
    public UUID getId() {
        return id;
    }
}
