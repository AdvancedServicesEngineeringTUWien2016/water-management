package at.ac.tuwien.ase2016.wm.model.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.AttributeConverter;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;

import com.blazebit.reflection.ReflectionUtils;

public abstract class AbstractConverterType<X, Y> implements EnhancedUserType, AttributeConverter<X, Y> {
	
	private final boolean mutable;
	private final int sqlType;
	private final Class<X> userTypeClass;
	
	@SuppressWarnings("unchecked")
	public AbstractConverterType(boolean mutable, int sqlType) {
		this.mutable = mutable;
		this.sqlType = sqlType;
		this.userTypeClass = (Class<X>) ReflectionUtils.resolveTypeVariable(getClass(), AbstractConverterType.class.getTypeParameters()[0]);
	}
	
	protected abstract X newObject(X object);

	protected abstract void set(PreparedStatement statement, int index, Y value) throws SQLException;

	protected abstract Y get(ResultSet rs, String name) throws SQLException;

	@Override
	@SuppressWarnings("unchecked")
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, sqlType);
		} else {	
			set(statement, index, convertToDatabaseColumn((X) value));
		}
	}
	
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		Y dbData = get(rs, names[0]);
		
		if (rs.wasNull()) {
			return null;
		}
		
		return convertToEntityAttribute(dbData);
	}
	
	@Override
	public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
		return cached;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object deepCopy(final Object o) throws HibernateException {
		return o == null ? null : newObject((X) o);
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
		return mutable;
	}

	@Override
	public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<X> returnedClass() {
		return userTypeClass;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { sqlType };
	}

	@Override
	public String toXMLString(Object value) {
		return null;
	}

	@Override
	public Object fromXMLString(String xmlValue) {
		return null;
	}
}