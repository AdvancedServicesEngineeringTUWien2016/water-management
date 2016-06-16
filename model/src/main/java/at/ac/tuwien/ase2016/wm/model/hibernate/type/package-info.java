@TypeDefs({
	@TypeDef(name = "point", defaultForType = at.ac.tuwien.ase2016.wm.model.geometry.Point.class, typeClass = at.ac.tuwien.ase2016.wm.model.hibernate.type.geometry.PointType.class),
		@TypeDef(name = "zonedDateTime", defaultForType = java.time.ZonedDateTime.class, typeClass = at.ac.tuwien.ase2016.wm.model.hibernate.type.time.ZonedDateTimeType.class)
})
package at.ac.tuwien.ase2016.wm.model.hibernate.type;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
