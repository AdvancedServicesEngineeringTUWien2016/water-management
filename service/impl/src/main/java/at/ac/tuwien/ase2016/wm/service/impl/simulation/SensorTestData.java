package at.ac.tuwien.ase2016.wm.service.impl.simulation;

import at.ac.tuwien.ase2016.wm.model.Customer;
import at.ac.tuwien.ase2016.wm.model.Sensor;
import at.ac.tuwien.ase2016.wm.model.SensorGroup;
import at.ac.tuwien.ase2016.wm.model.SensorSimulationConfiguration;
import at.ac.tuwien.ase2016.wm.model.geometry.Point;
import at.ac.tuwien.ase2016.wm.model.simulation.SensorAttribute;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Startup
@Singleton(name = "SensorTestData")
public class SensorTestData {

    @Inject
    private EntityManager em;

    @PostConstruct
    public void init() {
        SensorGroup vienna = new SensorGroup("Vienna sensors");
        SensorGroup vienna13 = new SensorGroup("Vienna 13 district sensors", vienna);
        SensorGroup vienna14 = new SensorGroup("Vienna 14 district sensors", vienna);
        SensorGroup vienna14Braunschweiggasse = new SensorGroup("Vienna 14 district sensors in Braunschweiggasse", vienna14);
        SensorGroup vienna14Leegasse = new SensorGroup("Vienna 14 district sensors in Leegasse", vienna14);
        SensorGroup vienna14Leegasse7 = new SensorGroup("Vienna 14 district sensors in Leegasse 7", vienna14Leegasse);
        SensorGroup vienna14Leegasse7_15 = new SensorGroup("Vienna 14 district sensors in Leegasse 7/15", vienna14Leegasse7);
        em.persist(vienna);
        em.persist(vienna13);
        em.persist(vienna14);
        em.persist(vienna14Braunschweiggasse);
        em.persist(vienna14Leegasse);
        em.persist(vienna14Leegasse7);
        em.persist(vienna14Leegasse7_15);

        Sensor viennaIn = edgeSensor(2000000);
        viennaIn.setGroup(vienna);
        viennaIn.setLocation(new Point(48.153493, 16.219318));

        Sensor viennaIn13 = edgeSensor(60000);
        viennaIn13.setGroup(vienna13);
        viennaIn13.setLocation(new Point(48.163839, 16.255725));

        Sensor viennaIn14 = edgeSensor(10000);
        viennaIn14.setGroup(vienna14);
        viennaIn14.setLocation(new Point(48.193177, 16.271819));

        Sensor viennaIn14Braunschweiggasse = edgeSensor(200);
        viennaIn14Braunschweiggasse.setGroup(vienna14Braunschweiggasse);
        viennaIn14Braunschweiggasse.setLocation(new Point(48.189405, 16.294834));

        Sensor viennaIn14Leegasse = edgeSensor(100);
        viennaIn14Leegasse.setGroup(vienna14Leegasse);
        viennaIn14Leegasse.setLocation(new Point(48.190484, 16.292918));

        Sensor viennaIn14Leegasse7 = edgeSensor(20);
        viennaIn14Leegasse7.setGroup(vienna14Leegasse7);
        viennaIn14Leegasse7.setLocation(new Point(48.191349, 16.293186));

        Sensor viennaIn14Leegasse7_15 = houseSensor();
        viennaIn14Leegasse7_15.setGroup(vienna14Leegasse7_15);
        viennaIn14Leegasse7_15.setLocation(new Point(48.191349, 16.293186));

        em.persist(viennaIn);
        em.persist(viennaIn13);
        em.persist(viennaIn14);
        em.persist(viennaIn14Braunschweiggasse);
        em.persist(viennaIn14Leegasse);
        em.persist(viennaIn14Leegasse7);
        em.persist(viennaIn14Leegasse7_15);

        // Create customer
        Customer customer = new Customer();
        customer.setName("Customer 1");
        customer.getSensorGroups().add(vienna14Leegasse7_15);
        em.persist(customer);
    }

    private Sensor edgeSensor(int maximumHouseholds) {
        Sensor s = new Sensor();
        // Sample every 5 seconds
        s.setSampleSeconds(5);
        s.getSimulationConfiguration()
                .put(SensorAttribute.PH_VALUE, SensorSimulationConfiguration.builder()
                                .withGood(6.5f, 9.5f)
                                .withException(0.001, 0f, 10f)
                                .withError(0.001, 1, 1)
                                .withPermanentError(0.001)
                                .build()
        );
        s.getSimulationConfiguration()
                .put(SensorAttribute.TEMPERATURE, SensorSimulationConfiguration.builder()
                        .withGood(10f, 20f)
                        .withException(0.001, 4f, 90f)
                        .withError(0.001, 1, 1)
                        .withPermanentError(0.001)
                        .build()
                );
        s.getSimulationConfiguration()
                .put(SensorAttribute.CONSUMPTION, SensorSimulationConfiguration.builder()
                        .withGood(4000f, 4000f * maximumHouseholds)
                        .withError(0.001, 1, 1)
                        .withPermanentError(0.001)
                        .build()
                );
        return s;
    }

    private Sensor houseSensor() {
        Sensor s = new Sensor();
        // Sample every 30 seconds
        s.setSampleSeconds(30);
        s.getSimulationConfiguration()
                .put(SensorAttribute.PH_VALUE, SensorSimulationConfiguration.builder()
                        .withGood(6.5f, 9.5f)
                        .withException(0.001, 0f, 10f)
                        .withError(0.001, 1, 1)
                        .withPermanentError(0.001)
                        .build()
                );
        s.getSimulationConfiguration()
                .put(SensorAttribute.TEMPERATURE, SensorSimulationConfiguration.builder()
                        .withGood(10f, 20f)
                        .withException(0.001, 4f, 90f)
                        .withError(0.001, 1, 1)
                        .withPermanentError(0.001)
                        .build()
                );
        s.getSimulationConfiguration()
                .put(SensorAttribute.CONSUMPTION, SensorSimulationConfiguration.builder()
                        .withGood(0f, 4000f)
                        .withError(0.001, 1, 1)
                        .withPermanentError(0.001)
                        .build()
                );
        return s;
    }

}
