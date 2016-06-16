package at.ac.tuwien.ase2016.wm.server.web;

import org.primefaces.model.chart.LineChartModel;

import java.util.ArrayList;
import java.util.List;

public class ChartModels {

    private final LineChartModel temperatureModel;
    private final LineChartModel phValueModel;
    private final LineChartModel consumptionModel;
    private final List<ErrorModel> temperatureErrorModel;
    private final List<ErrorModel> phValueErrorModel;
    private final List<ErrorModel> consumptionErrorModel;

    ChartModels() {
        this.temperatureModel = null;
        this.phValueModel = null;
        this.consumptionModel = null;
        this.temperatureErrorModel = null;
        this.phValueErrorModel = null;
        this.consumptionErrorModel = null;
    }

    public ChartModels(LineChartModel temperatureModel, LineChartModel phValueModel, LineChartModel consumptionModel, List<ErrorModel> temperatureErrorModel, List<ErrorModel> phValueErrorModel, List<ErrorModel> consumptionErrorModel) {
        this.temperatureModel = temperatureModel;
        this.phValueModel = phValueModel;
        this.consumptionModel = consumptionModel;
        this.temperatureErrorModel = temperatureErrorModel;
        this.phValueErrorModel = phValueErrorModel;
        this.consumptionErrorModel = consumptionErrorModel;
    }

    public LineChartModel getTemperatureModel() {
        return temperatureModel;
    }

    public LineChartModel getPhValueModel() {
        return phValueModel;
    }

    public LineChartModel getConsumptionModel() {
        return consumptionModel;
    }

    public List<ErrorModel> getTemperatureErrorModel() {
        return temperatureErrorModel;
    }

    public List<ErrorModel> getPhValueErrorModel() {
        return phValueErrorModel;
    }

    public List<ErrorModel> getConsumptionErrorModel() {
        return consumptionErrorModel;
    }
}
