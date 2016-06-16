package at.ac.tuwien.ase2016.wm.service.impl.simulation;

import at.ac.tuwien.ase2016.wm.model.*;
import at.ac.tuwien.ase2016.wm.model.simulation.SensorAttribute;
import at.ac.tuwien.ase2016.wm.service.api.simulation.SensorDataSimulator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Startup
@Singleton
@DependsOn("SensorTestData")
@Path("simulator")
public class SensorDataSimulatorImpl implements SensorDataSimulator {

    @Inject
    private EntityManager em;
    @Resource
    private TimerService timerService;

    private SensorSimulator[] sensorSimulators;
    private int smallestSampleSeconds = Integer.MAX_VALUE;
    private boolean running;

    @Override
    @PostConstruct
    public void init() {
        List<Sensor> sensors = em.createQuery("SELECT s FROM Sensor s ORDER BY s.sampleSeconds ASC").getResultList();
        sensorSimulators = new SensorSimulator[sensors.size()];

        for (int i = 0; i < sensors.size(); i++) {
            final Sensor s = sensors.get(i);
            smallestSampleSeconds = Math.min(smallestSampleSeconds, s.getSampleSeconds());
            sensorSimulators[i] = new SensorSimulator(s);
        }

        prepareData();
//        schedule();
    }

    @Override
    @POST
    @Path("schedule")
    public void schedule() {
        if (!running) {
            running = true;
            timerService.createTimer(TimeUnit.SECONDS.toMillis(smallestSampleSeconds), null);
        }
    }

    @Override
    @POST
    @Path("stop")
    @PreDestroy
    public void stop() {
        running = false;
        timerService.getAllTimers().forEach(t -> t.cancel());
    }

    @Override
    @POST
    @Path("problematic")
    public Response simulateProblem(@QueryParam("attribute") String attribute) {
        final Random r = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();
        sensorSimulators[sensorSimulators.length - 1].nextSampleTime = now;
        final SensorEvent event = sensorSimulators[sensorSimulators.length - 1].simulate(r, now);
        event.setSensor(em.getReference(Sensor.class, event.getSensor().getId()));

        if ("temperature".equals(attribute)) {
            event.setTemperatureCelsius(80f);
        } else if ("phValue".equals(attribute)) {
            event.setPhValue(5f);
        } else if ("consumption".equals(attribute)) {
            event.setConsumptionMilliLiter(4000f);
        }

        em.persist(event);
        em.flush();

        return Response.ok().build();
    }

    @Override
    @Timeout
    public void timeout() {
        if (!running) {
            return;
        }

        final Random r = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();

        LocalDateTime nextEvent = simulateRun(r, now);
        em.flush();

        final long duration = LocalDateTime.now().until(nextEvent, ChronoUnit.MILLIS);
        timerService.createTimer(duration < 0 ? 0 : duration, null);
    }

    private void prepareData() {
        final Random r = ThreadLocalRandom.current();
        LocalDateTime now = LocalDateTime.now().minusHours(1);

        for (int i = 0; i < sensorSimulators.length; i++) {
            SensorSimulator simulator = sensorSimulators[i];
            simulator.nextSampleTime = now.minusSeconds(1);
        }

        for (int i = 0; i < 240; i++) {
            simulateRun(r, now);
            now = now.plusSeconds(15);

            if (i % 4 == 0) {
                em.flush();
            }
        }

        em.flush();
    }

    private LocalDateTime simulateRun(Random r, LocalDateTime now) {
        LocalDateTime nextEvent = now.plusSeconds(smallestSampleSeconds);

        for (int i = 0; i < sensorSimulators.length; i++) {
            SensorSimulator simulator = sensorSimulators[i];
            final SensorEvent event = simulator.simulate(r, now);

            if (simulator.nextSampleTime.isBefore(nextEvent)) {
                nextEvent = simulator.nextSampleTime;
            }

            if (event != null) {
                event.setSensor(em.getReference(Sensor.class, event.getSensor().getId()));
                em.persist(event);
            }
        }

        return nextEvent;
    }

    static class SensorSimulator {

        final Sensor sensor;
        final int sampleSeconds;
        final SensorAttributeSimulator[] attributeSimulators;
        LocalDateTime nextSampleTime;

        SensorSimulator(Sensor s) {
            this.sensor = s;
            this.sampleSeconds = s.getSampleSeconds();

            Map<SensorAttribute, SensorSimulationConfiguration> c = s.getSimulationConfiguration();
            this.attributeSimulators = new SensorAttributeSimulator[c.size()];

            int i = 0;
            for (Map.Entry<SensorAttribute, SensorSimulationConfiguration> entry : c.entrySet()) {
                attributeSimulators[i] = new SensorAttributeSimulator(entry.getKey(), entry.getValue());
                i++;
            }


            this.nextSampleTime = LocalDateTime.now().plusSeconds(sampleSeconds);
        }

        SensorEvent simulate(final Random r, final LocalDateTime now) {
            if (nextSampleTime.isAfter(now)) {
                return null;
            }

            nextSampleTime = nextSampleTime.plusSeconds(sampleSeconds);

            SensorStreamInsertEvent event = new SensorStreamInsertEvent();
            event.setSensor(sensor);
            event.setArrivalTimestamp(Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant()));
            event.setDataFrom(now.atZone(ZoneId.systemDefault()).minusSeconds(sampleSeconds));
            event.setDataTo(now.atZone(ZoneId.systemDefault()));

            // TODO: other attributes

            for (int i = 0; i < attributeSimulators.length; i++) {
                attributeSimulators[i].simulate(r, now, event);
            }

            return event;
        }
    }

    static class SensorAttributeSimulator {
        final SensorAttribute attribute;
        final double permanentErrorProbabilityEnd;
        final double errorProbabilityEnd;
        final double exceptionProbabilityEnd;
        final double exceptionLowProbabilityEnd;

        final int outageSeconds;
        final int outageSecondsVariance;

        final float goodStart;
        final float goodMultiplier;

        final float exceptionStartStart;
        final float exceptionStartMultiplier;
        final float exceptionEndStart;
        final float exceptionEndMultiplier;

        boolean permanentError;
        LocalDateTime erroredUntil;

        SensorAttributeSimulator(SensorAttribute attribute, SensorSimulationConfiguration c) {
            this.attribute = attribute;
            this.permanentErrorProbabilityEnd = c.getPermanentErrorProbability() == null ? 0 : c.getPermanentErrorProbability().doubleValue();
            this.errorProbabilityEnd = permanentErrorProbabilityEnd + (c.getErrorProbability() == null ? 0 : c.getErrorProbability().doubleValue());
            this.exceptionProbabilityEnd = errorProbabilityEnd + (c.getExceptionProbability() == null ? 0 : c.getExceptionProbability().doubleValue());
            this.exceptionLowProbabilityEnd = 0; // TODO: Just always use the upper bound for now

            this.outageSeconds = c.getOutageSeconds() == null ? 0 : c.getOutageSeconds();
            this.outageSecondsVariance = c.getOutageSecondsVariance() == null ? 0 : c.getOutageSecondsVariance();

            this.goodStart = c.getGoodStart();
            this.goodMultiplier = c.getGoodEnd() - goodStart;

            if (c.getExceptionProbability() == null) {
                this.exceptionStartStart = 0;
                this.exceptionStartMultiplier = 0;
                this.exceptionEndStart = 0;
                this.exceptionEndMultiplier = 0;
            } else {
                this.exceptionStartStart = c.getLowest();
                this.exceptionStartMultiplier = c.getGoodStart() - exceptionStartStart;
                this.exceptionEndStart = c.getGoodEnd();
                this.exceptionEndMultiplier = c.getHighest() - exceptionEndStart;
            }
        }

        void simulate(final Random r, final LocalDateTime now, final SensorEvent event) {
            if (permanentError) {
                return;
            }

            if (erroredUntil != null) {
                if (erroredUntil.isBefore(now)) {
                    return;
                }

                erroredUntil = null;
            }

            final double actionRandom = r.nextDouble();

            if (actionRandom < permanentErrorProbabilityEnd) {
                permanentError = true;
            } else if (actionRandom < errorProbabilityEnd) {
                int seconds = outageSeconds + (r.nextInt(2 * outageSecondsVariance) - outageSecondsVariance);
                erroredUntil = now.plusSeconds(seconds);
            } else if (actionRandom < exceptionProbabilityEnd) {
                final float value;

                if (exceptionLowProbabilityEnd < r.nextDouble()) {
                    value = exceptionStartStart + (r.nextFloat() * exceptionStartMultiplier);
                } else {
                    value = exceptionEndStart + (r.nextFloat() * exceptionEndMultiplier);
                }

                attribute.setFloat(event, value);
            } else {
                final float value = goodStart + (r.nextFloat() * goodMultiplier);
                attribute.setFloat(event, value);
            }

        }
    }

}
