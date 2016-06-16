package at.ac.tuwien.ase2016.wm.model;

import java.io.Serializable;

public interface IdHolder<I extends Serializable> extends Serializable {

    public I getId();

}
