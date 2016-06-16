package at.ac.tuwien.ase2016.wm.service.api.simulation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Timeout;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public interface SensorDataSimulator {

    void init();

    @POST
    @Path("schedule")
    void schedule();

    @Path("stop")
    void stop();

    @POST
    @Path("problematic")
    Response simulateProblem(@QueryParam("attribute") String attribute);

    void timeout();
}
