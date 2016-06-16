package at.ac.tuwien.ase2016.wm.service.impl.alert;

import at.ac.tuwien.ase2016.wm.service.api.alert.AlertEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class AlertManager implements Runnable {

    private static final Logger LOG = Logger.getLogger(AlertManager.class.getName());

    @Resource
    private ManagedExecutorService executorService;

    @Resource(mappedName = "java:jboss/datasources/ASE2016-WM")
    private DataSource dataSource;

    @Inject
    private Event<AlertEvent> alertEvent;

    private volatile Future<?> future;
    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        future = executorService.submit(this);
    }

    @PreDestroy
    public void destroy() {
        running = false;
        future.cancel(true);
    }

    @Override
    public void run() {
        while (running) {
            try (Connection connection = dataSource.getConnection()) {
                org.postgresql.PGConnection pgConnection = connection.unwrap(org.postgresql.PGConnection.class);

                try (Statement stmt = connection.createStatement()){
                    stmt.execute("LISTEN alert");
                }

                while (running) {
                    // issue a dummy query to contact the backend
                    // and receive any pending notifications.
                    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    }

                    org.postgresql.PGNotification[] notifications = pgConnection.getNotifications();
                    if (notifications != null) {
                        for (int i = 0; i < notifications.length; i++) {
                            LOG.info("Got notification: " + notifications[i].getName() + notifications[i].getParameter());
                            alertEvent.fire(new AlertEvent(UUID.fromString(notifications[i].getParameter())));
                        }
                    }

                    // wait a while before checking again for new notifications
                    Thread.sleep(500);
                }
            } catch (SQLException e) {
                LOG.log(Level.SEVERE, "Error when listening!", e);
            } catch (InterruptedException e) {
                // We probably got canceled
            }
        }
    }
}
