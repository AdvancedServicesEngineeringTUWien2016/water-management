package at.ac.tuwien.ase2016.wm.service.impl.simulation;

import at.ac.tuwien.ase2016.wm.model.Configuration;
import at.ac.tuwien.ase2016.wm.model.Sensor;
import at.ac.tuwien.ase2016.wm.model.SensorGroup;
import at.ac.tuwien.ase2016.wm.model.SensorSimulationConfiguration;
import at.ac.tuwien.ase2016.wm.model.geometry.Point;
import at.ac.tuwien.ase2016.wm.model.simulation.SensorAttribute;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Startup
@Singleton(name = "SensorAnalyticsSetup")
public class SensorAnalyticsSetup {

    @Inject
    private EntityManager em;

    @PostConstruct
    public void init() {
        Connection con = em.unwrap(org.hibernate.engine.spi.SessionImplementor.class).connection();
        try (Statement s = con.createStatement()) {
            if (Configuration.usePipelineDb) {
                s.execute("DROP TRIGGER IF EXISTS micro_dc_bad_quality_alert ON micro_dc_bad_quality_alert");
                s.execute("DROP FUNCTION IF EXISTS bad_quality_alert()");
                s.execute("DROP CONTINUOUS VIEW IF EXISTS micro_dc_aggregated_sensor_event");
                s.execute("DROP CONTINUOUS VIEW IF EXISTS micro_dc_bad_quality_alert");
                s.execute("DROP STREAM IF EXISTS micro_dc_sensor_event");

                s.execute("CREATE STREAM micro_dc_sensor_event (" +
                        "sensor_id UUID, " +
                        "data_from TIMESTAMPTZ, " +
                        "data_to TIMESTAMPTZ, " +
                        "consumption_milli_liter FLOAT, " +
                        "temperature_celsius FLOAT, " +
                        "ph_value FLOAT" +
                        ")");

                s.execute("CREATE CONTINUOUS VIEW micro_dc_bad_quality_alert AS " +
                        "SELECT " +
                        "e.sensor_id, " +
                        "MAX(e.arrival_timestamp) OVER w AS arrived_timestamp, " +
                        "COUNT(e.sensor_id) OVER (PARTITION BY e.sensor_id) AS row_number, " +
                        "AVG(e.temperature_celsius) OVER w AS temperature_celsius, " +
                        "MIN(data_from) OVER w AS data_from, " +
                        "MAX(data_to) OVER w AS data_to " +
                        "FROM micro_dc_sensor_event e " +
                        "WINDOW w AS (PARTITION BY e.sensor_id ROWS BETWEEN 4 PRECEDING AND CURRENT ROW)");

                s.execute("CREATE CONTINUOUS VIEW micro_dc_aggregated_sensor_event AS " +
                        "SELECT e.sensor_id, MIN(data_from) AS data_from, MAX(data_to) AS data_to, SUM(consumption_milli_liter) AS consumption_milli_liter, AVG(temperature_celsius) AS temperature_celsius, AVG(ph_value) AS ph_value " +
                        "FROM micro_dc_sensor_event e GROUP BY e.sensor_id, date_trunc('minute', e.data_from)");

                s.execute(
                        "CREATE FUNCTION bad_quality_alert() RETURNS TRIGGER AS $test_update$\n" +
                                "DECLARE\n" +
                                "alertId uuid := md5(random()::text || clock_timestamp()::text)::uuid;\n" +
                                "BEGIN\n" +
                                "INSERT INTO sensor_alert(id, type, created_at, sensor_id, data_from, data_to, read) " +
                                "VALUES(alertId, 'BAD_WATER_QUALITY', CURRENT_TIMESTAMP, NEW.sensor_id, NEW.data_from, NEW.data_to, false);\n" +
                                "SELECT pg_notify('alert', alertId::varchar);\n" +
                                "RETURN NEW;\n" +
                                "END;\n" +
                                "$test_update$ LANGUAGE plpgsql");
                s.execute("CREATE TRIGGER micro_dc_bad_quality_alert AFTER UPDATE OR INSERT ON micro_dc_bad_quality_alert FOR ROW\n" +
                        "WHEN (NEW.row_number > 4 AND NEW.temperature_celsius NOT BETWEEN 10 AND 20)\n" +
                        "EXECUTE PROCEDURE bad_quality_alert();");
            } else {
                s.execute("DROP TRIGGER IF EXISTS micro_dc_bad_quality_alert_workaround ON micro_dc_sensor_event");
                s.execute("DROP TRIGGER IF EXISTS aggregated_sensor_event_workaround ON micro_dc_sensor_event");
                s.execute("DROP FUNCTION IF EXISTS bad_quality_alert_workaround()");
                s.execute("DROP FUNCTION IF EXISTS aggregated_sensor_event_workaround()");
                s.execute("DROP VIEW IF EXISTS micro_dc_bad_quality_alert_workaround");

                s.execute("CREATE VIEW micro_dc_bad_quality_alert_workaround AS " +
                        "SELECT " +
                            "e.sensor_id, " +
                            "MAX(e.arrival_timestamp) OVER w AS arrived_timestamp, " +
                            "row_number() OVER (PARTITION BY e.sensor_id) AS row_number, " +
                            "AVG(e.temperature_celsius) OVER w AS temperature_celsius, " +
                            "AVG(e.ph_value) OVER w AS ph_value, " +
                            "MIN(data_from) OVER w AS data_from, " +
                            "MAX(data_to) OVER w AS data_to " +
                        "FROM micro_dc_sensor_event e " +
                        "WINDOW w AS (PARTITION BY e.sensor_id ROWS BETWEEN 4 PRECEDING AND CURRENT ROW)");
                s.execute(
                        "CREATE FUNCTION bad_quality_alert_workaround() RETURNS TRIGGER AS $BODY$\n" +
                            "DECLARE\n" +
                                "row_number bigint;\n" +
                                "temperature_celsius float;\n" +
                                "ph_value float;\n" +
                                "alertId uuid;\n" +
                            "BEGIN\n" +
                                "" +
                                "SELECT a.row_number, a.temperature_celsius, a.ph_value INTO row_number, temperature_celsius, ph_value " +
                                "FROM micro_dc_bad_quality_alert_workaround a " +
                                "WHERE (a.sensor_id, a.arrived_timestamp) = (NEW.sensor_id, NEW.arrival_timestamp);\n" +
                                "" +
                                "IF row_number > 4 AND temperature_celsius NOT BETWEEN 10 AND 20 THEN \n" +
                                    "alertId := md5(random()::text || clock_timestamp()::text)::uuid;" +
                                    "INSERT INTO sensor_alert(id, type, created_at, sensor_id, data_from, data_to, read) " +
                                    "VALUES(alertId, 'BAD_WATER_QUALITY', CURRENT_TIMESTAMP, NEW.sensor_id, NEW.data_from, NEW.data_to, false);\n" +
                                    "PERFORM pg_notify('alert', alertId::varchar);\n" +
                                "END IF;\n" +
                                "" +
                                "IF row_number > 4 AND ph_value NOT BETWEEN 6.5 AND 9.5 THEN \n" +
                                    "alertId := md5(random()::text || clock_timestamp()::text)::uuid;" +
                                    "INSERT INTO sensor_alert(id, type, created_at, sensor_id, data_from, data_to, read) " +
                                    "VALUES(alertId, 'BAD_WATER_QUALITY', CURRENT_TIMESTAMP, NEW.sensor_id, NEW.data_from, NEW.data_to, false);\n" +
                                    "PERFORM pg_notify('alert', alertId::varchar);\n" +
                                "END IF;\n" +
                                "RETURN NEW;\n" +
                            "END;\n" +
                        "$BODY$ LANGUAGE plpgsql");
                s.execute("CREATE TRIGGER micro_dc_bad_quality_alert_workaround AFTER UPDATE OR INSERT ON micro_dc_sensor_event FOR ROW\n" +
                        "EXECUTE PROCEDURE bad_quality_alert_workaround();");


                s.execute(
                        "CREATE FUNCTION aggregated_sensor_event_workaround() RETURNS TRIGGER AS $BODY$\n" +
                            "BEGIN\n" +
                                "INSERT INTO sensor_aggregate_event(sensor_id, period_timestamp, data_from, data_to, element_count, consumption_milli_liter, temperature_celsius, temperature_celsius_sum, ph_value, ph_value_sum) " +
                                "VALUES(NEW.sensor_id, date_trunc('minute', NEW.data_from), NEW.data_from, NEW.data_to, 1, NEW.consumption_milli_liter, NEW.temperature_celsius, NEW.temperature_celsius, NEW.ph_value, NEW.ph_value) " +
                                "ON CONFLICT(sensor_id, period_timestamp) " +
                                "DO UPDATE SET " +
                                    "data_from = LEAST(sensor_aggregate_event.data_from, EXCLUDED.data_from), " +
                                    "data_to = GREATEST(sensor_aggregate_event.data_to, EXCLUDED.data_to), " +
                                    "element_count = sensor_aggregate_event.element_count + EXCLUDED.element_count, " +
                                    "consumption_milli_liter = sensor_aggregate_event.consumption_milli_liter + EXCLUDED.consumption_milli_liter, " +
                                    "temperature_celsius = (sensor_aggregate_event.temperature_celsius_sum + EXCLUDED.temperature_celsius) / (sensor_aggregate_event.element_count + 1), " +
                                    "temperature_celsius_sum = sensor_aggregate_event.temperature_celsius_sum + EXCLUDED.temperature_celsius, " +
                                    "ph_value = (sensor_aggregate_event.ph_value_sum + EXCLUDED.ph_value) / (sensor_aggregate_event.element_count + 1), " +
                                    "ph_value_sum = sensor_aggregate_event.ph_value_sum + EXCLUDED.ph_value" +
                                ";" +
                                "RETURN NEW;\n" +
                            "END;\n" +
                        "$BODY$ LANGUAGE plpgsql");
                s.execute("CREATE TRIGGER aggregated_sensor_event_workaround AFTER UPDATE OR INSERT ON micro_dc_sensor_event FOR ROW\n" +
                        "EXECUTE PROCEDURE aggregated_sensor_event_workaround();");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        Connection con = em.unwrap(org.hibernate.engine.spi.SessionImplementor.class).connection();
        try (Statement s = con.createStatement()) {
            if (!Configuration.usePipelineDb) {
                s.execute("DROP TRIGGER IF EXISTS micro_dc_bad_quality_alert_workaround ON micro_dc_sensor_event");
                s.execute("DROP TRIGGER IF EXISTS aggregated_sensor_event_workaround ON micro_dc_sensor_event");
                s.execute("DROP FUNCTION IF EXISTS bad_quality_alert_workaround()");
                s.execute("DROP FUNCTION IF EXISTS aggregated_sensor_event_workaround()");
                s.execute("DROP VIEW IF EXISTS micro_dc_bad_quality_alert_workaround");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String AVG(String v) {
        return "(" + v + "[2] / " + v + "[1])";
    }
}
