package at.ac.tuwien.ase2016.wm.model.hibernate.type.geometry;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import at.ac.tuwien.ase2016.wm.model.geometry.Point;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class PointType implements UserType {
	
	private final Constructor<?> pgPointConstructor;
	
	public PointType() {
		try {
			this.pgPointConstructor = Class.forName("org.postgresql.geometric.PGpoint").getConstructor(double.class, double.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Object createNativePoint(Point p) {
		try {
			return pgPointConstructor.newInstance(p.getX(), p.getY());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, Types.OTHER);
		} else {
			Point p = (Point) value;
			Object nativePoint = createNativePoint(p);
			statement.setObject(index, nativePoint);
		}
	}
	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String dbData = rs.getString(names[0]);
		
		if (rs.wasNull()) {
			return null;
		}
		
		return Point.fromString(dbData);
	}
	
	@Override
	public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(final Object o) throws HibernateException {
		return o == null ? null : new Point((Point) o);
	}

	@Override
	public Serializable disassemble(final Object o) throws HibernateException {
		return (Serializable) o;
	}

	@Override
	public boolean equals(final Object x, final Object y) throws HibernateException {
		return x == null ? y == null : x.equals(y);
	}

	@Override
	public int hashCode(final Object o) throws HibernateException {
		return o == null ? 0 : o.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return Point.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}

}
