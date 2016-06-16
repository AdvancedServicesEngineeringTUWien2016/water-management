package at.ac.tuwien.ase2016.wm.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseEntity<I extends Serializable> implements IdHolder<I> {

    protected I id;

    public BaseEntity() {
    }

    public BaseEntity(I id) {
        this.id = id;
    }

    public void setId(I id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity<?> that = (BaseEntity<?>) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
