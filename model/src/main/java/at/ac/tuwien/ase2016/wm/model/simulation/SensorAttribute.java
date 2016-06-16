package at.ac.tuwien.ase2016.wm.model.simulation;

import at.ac.tuwien.ase2016.wm.model.SensorEvent;

public enum SensorAttribute {
    CONSUMPTION {
        public void setFloat(SensorEvent event, float value) {
            event.setConsumptionMilliLiter(value);
        }
    },
    TEMPERATURE {
        public void setFloat(SensorEvent event, float value) {
            event.setTemperatureCelsius(value);
        }
    },
    PH_VALUE {
        public void setFloat(SensorEvent event, float value) {
            event.setPhValue(value);
        }
    };

    public void setFloat(SensorEvent event, float value) {
        throw new IllegalArgumentException("The attribute '" + name() + "' is not of type 'FLOAT'");
    }
}
