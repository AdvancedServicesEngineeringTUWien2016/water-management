package at.ac.tuwien.ase2016.wm.model.hibernate.type.time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import at.ac.tuwien.ase2016.wm.model.hibernate.type.AbstractConverterType;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserVersionType;

public class ZonedDateTimeType extends AbstractConverterType<ZonedDateTime, Timestamp> implements UserVersionType {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S VV", Locale.ENGLISH);

	public ZonedDateTimeType() {
		super(false, Types.TIMESTAMP);
	}

	@Override
	public Timestamp convertToDatabaseColumn(ZonedDateTime attribute) {
		if (attribute == null) {
			return null;
		}
		
		return Timestamp.from(attribute.toInstant());
	}

	@Override
	public ZonedDateTime convertToEntityAttribute(Timestamp dbData) {
		if (dbData == null) {
			return null;
		}
		
		return ZonedDateTime.ofInstant(dbData.toInstant(), ZoneId.systemDefault());
	}

	@Override
	public String objectToSQLString(Object value) {
		return "{ts '" + FORMATTER.format((ZonedDateTime) value) + "'}";
	}

	@Override
	protected ZonedDateTime newObject(ZonedDateTime object) {
		return object;
	}

	@Override
	protected void set(PreparedStatement statement, int index, Timestamp value) throws SQLException {
		statement.setTimestamp(index, value);
	}

	@Override
	protected Timestamp get(ResultSet rs, String name) throws SQLException {
		return rs.getTimestamp(name);
	}

	@Override
	public int compare(Object o1, Object o2) {
		return ((ZonedDateTime) o1).compareTo((ZonedDateTime) o2);
	}

	@Override
	public Object seed(SessionImplementor session) {
		return ZonedDateTime.now();
	}

	@Override
	public Object next(Object current, SessionImplementor session) {
		return ZonedDateTime.now();
	}

}
