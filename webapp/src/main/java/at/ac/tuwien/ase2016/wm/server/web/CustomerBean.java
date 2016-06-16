package at.ac.tuwien.ase2016.wm.server.web;

import at.ac.tuwien.ase2016.wm.model.SensorAggregateEvent;
import at.ac.tuwien.ase2016.wm.service.api.simulation.SensorDataSimulator;
import org.primefaces.model.chart.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@ViewScoped
public class CustomerBean implements Serializable {

    private static final DateTimeFormatter format = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .toFormatter();

    private List<UUID> sensorIds;

    @Inject
    private CustomerDataAccess dataAccess;
    @Inject
    private SensorDataSimulator simulator;

    @PostConstruct
    private void init() {
        sensorIds = dataAccess.getSensors();
    }

    public List<UUID> getSensorIds() {
        return sensorIds;
    }

    public void startSimulation() {
        simulator.schedule();
    }

    public void stopSimulation() {
        simulator.stop();
    }

    public void simulationProblem() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            simulator.simulateProblem("phValue");
        } else {
            simulator.simulateProblem("temperature");
        }
    }

    @Produces
    @RequestScoped
    @Named("customerChartModels")
    public ChartModels createModels() {

        LineChartModel temperatureModel = new LineChartModel();
        LineChartModel phValueModel = new LineChartModel();
        LineChartModel consumptionModel = new LineChartModel();
        List<ErrorModel> temperatureErrorModel = new ArrayList<>();
        List<ErrorModel> phValueErrorModel = new ArrayList<>();
        List<ErrorModel> consumptionErrorModel = new ArrayList<>();

        List<SensorAggregateEvent> events = dataAccess.getSensorData(sensorIds);
        Map<String, List<SensorAggregateEvent>> eventsBySensor = events.stream().collect(Collectors.groupingBy(e -> e.getSensorId().toString()));
        List<ZonedDateTime> dates = eventsBySensor.entrySet().stream()
                .flatMap(e -> {
                    return Stream.of(
                            e.getValue().get(0).getPeriodBucket(),
                            e.getValue().get(e.getValue().size() - 1).getPeriodBucket()
                    );
                }).sorted().collect(Collectors.toList());
        ZonedDateTime earliestDate = dates.isEmpty() ? LocalDateTime.MAX.atZone(ZoneId.systemDefault()) : dates.get(0);
        ZonedDateTime latestDate = dates.isEmpty() ? LocalDateTime.MIN.atZone(ZoneId.systemDefault()) : dates.get(dates.size() - 1);
        float[] maxConsumption = new float[1];

        eventsBySensor.entrySet().forEach(entry -> {
            LineChartSeries temperatureSeries = new LineChartSeries();
            temperatureSeries.setFill(true);
            temperatureSeries.setLabel(entry.getKey());
            temperatureModel.addSeries(temperatureSeries);

            LineChartSeries phValueSeries = new LineChartSeries();
            phValueSeries.setFill(true);
            phValueSeries.setLabel(entry.getKey());
            phValueModel.addSeries(phValueSeries);

            LineChartSeries consumptionSeries = new LineChartSeries();
            consumptionSeries.setFill(true);
            consumptionSeries.setLabel(entry.getKey());
            consumptionModel.addSeries(consumptionSeries);

            // Assumption that the list is sorted
            List<SensorAggregateEvent> list = entry.getValue();
            int index = 0;
            for (ZonedDateTime current = earliestDate; current.isBefore(latestDate); current = current.plusMinutes(1)) {
                String periodBucket = format.format(current);
                SensorAggregateEvent e = list.get(index);

                final float temperatureCelsius;
                final float phValue;
                final float consumptionMilliLiter;

                if (e.getPeriodBucket().equals(current)) {
                    temperatureCelsius = e.getTemperatureCelsius();
                    phValue = e.getPhValue();
                    consumptionMilliLiter = e.getConsumptionMilliLiter();
                    index++;
                } else {
                    // Missing values
                    temperatureCelsius = 0;
                    phValue = 0;
                    consumptionMilliLiter = 0;
                }

                temperatureSeries.set(periodBucket, temperatureCelsius);
                phValueSeries.set(periodBucket, phValue);
                consumptionSeries.set(periodBucket, consumptionMilliLiter);

                if (temperatureCelsius < 10 || temperatureCelsius > 20) {
                    addErrorModel(temperatureErrorModel, current, current.plusMinutes(1));
                }
                if (phValue < 6.5 || phValue > 9.5) {
                    addErrorModel(phValueErrorModel, current, current.plusMinutes(1));
                }

                maxConsumption[0] = Math.max(maxConsumption[0], consumptionMilliLiter);
            }
        });

        temperatureModel.setTitle("Temperature history");
        temperatureModel.setLegendPosition("ne");
        temperatureModel.setLegendPlacement(LegendPlacement.OUTSIDE);
        temperatureModel.setStacked(true);
        temperatureModel.setShowPointLabels(true);

        phValueModel.setTitle("PH-Value history");
        phValueModel.setLegendPosition("ne");
        phValueModel.setLegendPlacement(LegendPlacement.OUTSIDE);
        phValueModel.setStacked(true);
        phValueModel.setShowPointLabels(true);

        consumptionModel.setTitle("Water consumption history");
        consumptionModel.setLegendPosition("ne");
        consumptionModel.setLegendPlacement(LegendPlacement.OUTSIDE);
        consumptionModel.setStacked(true);
        consumptionModel.setShowPointLabels(true);

        // X-Axis is the same for all models
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMin(format.format(earliestDate.minusMinutes(2)));
        axis.setMax(format.format(latestDate.plusMinutes(2)));
        axis.setTickFormat("%H:%#M:%S");

        temperatureModel.getAxes().put(AxisType.X, axis);
        phValueModel.getAxes().put(AxisType.X, axis);
        consumptionModel.getAxes().put(AxisType.X, axis);

        // Y-Axis
        Axis temperatureYAxis = temperatureModel.getAxis(AxisType.Y);
        temperatureYAxis.setLabel("Temperature in Celsius");
        temperatureYAxis.setMin(0);
        temperatureYAxis.setMax(100);

        Axis phValueYAxis = phValueModel.getAxis(AxisType.Y);
        phValueYAxis.setLabel("PH-Value");
        phValueYAxis.setMin(0);
        phValueYAxis.setMax(10);

        Axis consumptionYAxis = consumptionModel.getAxis(AxisType.Y);
        consumptionYAxis.setLabel("Water consumption in ml");
        consumptionYAxis.setMin(0);
        consumptionYAxis.setMax(Math.max(15000, maxConsumption[0]));

        return new ChartModels(temperatureModel, phValueModel, consumptionModel, temperatureErrorModel, phValueErrorModel, consumptionErrorModel);
    }

    private void addErrorModel(List<ErrorModel> list, ZonedDateTime start, ZonedDateTime end) {
        if (list.isEmpty()) {
            list.add(new ErrorModel(start, end));
            return;
        }

        ErrorModel m = list.get(list.size() - 1);

        if (start.isBefore(m.getEnd()) || start.equals(m.getEnd())) {
            m.setEnd(end);
        } else {
            list.add(new ErrorModel(start, end));
        }
    }

    @Produces
    @RequestScoped
    @Named("customerTemperatureChartModel")
    public LineChartModel createTemperatureModel(@Named("customerChartModels") ChartModels models) {
        return models.getTemperatureModel();
    }

    @Produces
    @RequestScoped
    @Named("customerTemperatureErrorModel")
    public String createTemperatureErrorModel(@Named("customerChartModels") ChartModels models) {
        return errorModel(models.getTemperatureErrorModel());
    }

    @Produces
    @RequestScoped
    @Named("customerPhValueChartModel")
    public LineChartModel createPhValueModel(@Named("customerChartModels") ChartModels models) {
        return models.getPhValueModel();
    }

    @Produces
    @RequestScoped
    @Named("customerPhValueErrorModel")
    public String createPhValueErrorModel(@Named("customerChartModels") ChartModels models) {
        return errorModel(models.getPhValueErrorModel());
    }

    @Produces
    @RequestScoped
    @Named("customerConsumptionChartModel")
    public LineChartModel createConsumptionModel(@Named("customerChartModels") ChartModels models) {
        return models.getConsumptionModel();
    }

    @Produces
    @RequestScoped
    @Named("customerConsumptionErrorModel")
    public String createConsumptionErrorModel(@Named("customerChartModels") ChartModels models) {
        return errorModel(models.getConsumptionErrorModel());
    }

    private String errorModel(List<ErrorModel> model) {
        if (model.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ErrorModel m : model) {
            sb.append("{ line: {");

            sb.append("start: [new Date('").append(format.format(m.getStart().minusSeconds(7))).append("').getTime(), 20],");
            sb.append("stop: [new Date('").append(format.format(m.getEnd().minusSeconds(7))).append("').getTime(), 20],");
            sb.append("lineWidth: 1000,");
            sb.append("color: 'rgba(255, 0, 0,0.45)',");
            sb.append("shadow: false,");
            sb.append("lineCap: 'butt'");

            sb.append("}");
            sb.append("},");
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml";
    }
}
