package at.ac.tuwien.ase2016.wm.model.view;

import java.io.Serializable;

import com.blazebit.persistence.view.IdMapping;

public interface IdHolderView<I> extends Serializable {
	
	@IdMapping("id")
    public I getId();
}
