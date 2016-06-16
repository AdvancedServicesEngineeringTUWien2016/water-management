package at.ac.tuwien.ase2016.wm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class SensorSimulationConfiguration implements Serializable {

    private BigDecimal exceptionProbability;
    private BigDecimal errorProbability;
    private BigDecimal permanentErrorProbability;

    private Float goodStart;
    private Float goodEnd;
    private Float lowest;
    private Float highest;
    private Integer outageSeconds;
    private Integer outageSecondsVariance;

    public static interface Builder {

        Builder withGood(float start, float end);

        Builder withException(double probability, float lowest, float highest);

        Builder withError(double probability, int outageSeconds, int diff);

        Builder withPermanentError(double probability);

        SensorSimulationConfiguration build();

    }

    public static Builder builder() {
        final SensorSimulationConfiguration configuration = new SensorSimulationConfiguration();
        return new Builder() {
            @Override
            public Builder withGood(float start, float end) {
                configuration.goodStart = start;
                configuration.goodEnd = end;
                return this;
            }

            @Override
            public Builder withException(double probability, float lowest, float highest) {
                configuration.exceptionProbability = BigDecimal.valueOf(probability);
                configuration.lowest = lowest;
                configuration.highest = highest;
                return this;
            }

            @Override
            public Builder withError(double probability, int outageSeconds, int diffoutageSecondsVariance) {
                configuration.errorProbability = BigDecimal.valueOf(probability);
                configuration.outageSeconds = outageSeconds;
                configuration.outageSecondsVariance = diffoutageSecondsVariance;
                return this;
            }

            @Override
            public Builder withPermanentError(double probability) {
                configuration.permanentErrorProbability = BigDecimal.valueOf(probability);
                return this;
            }

            @Override
            public SensorSimulationConfiguration build() {
                return configuration;
            }
        };
    }

    @Column(name = "exception_probability")
    public BigDecimal getExceptionProbability() {
        return exceptionProbability;
    }

    public void setExceptionProbability(BigDecimal exceptionProbability) {
        this.exceptionProbability = exceptionProbability;
    }

    @Column(name = "error_probability")
    public BigDecimal getErrorProbability() {
        return errorProbability;
    }

    public void setErrorProbability(BigDecimal errorProbability) {
        this.errorProbability = errorProbability;
    }

    @Column(name = "permanent_error_probability")
    public BigDecimal getPermanentErrorProbability() {
        return permanentErrorProbability;
    }

    public void setPermanentErrorProbability(BigDecimal permanentErrorProbability) {
        this.permanentErrorProbability = permanentErrorProbability;
    }

    @Column(name = "good_start")
    public Float getGoodStart() {
        return goodStart;
    }

    public void setGoodStart(Float goodStart) {
        this.goodStart = goodStart;
    }

    @Column(name = "good_end")
    public Float getGoodEnd() {
        return goodEnd;
    }

    public void setGoodEnd(Float goodEnd) {
        this.goodEnd = goodEnd;
    }

    public Float getLowest() {
        return lowest;
    }

    public void setLowest(Float lowest) {
        this.lowest = lowest;
    }

    public Float getHighest() {
        return highest;
    }

    public void setHighest(Float highest) {
        this.highest = highest;
    }

    @Column(name = "outage_seconds")
    public Integer getOutageSeconds() {
        return outageSeconds;
    }

    public void setOutageSeconds(Integer outageSeconds) {
        this.outageSeconds = outageSeconds;
    }

    @Column(name = "outage_seconds_variance")
    public Integer getOutageSecondsVariance() {
        return outageSecondsVariance;
    }

    public void setOutageSecondsVariance(Integer outageSecondsVariance) {
        this.outageSecondsVariance = outageSecondsVariance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorSimulationConfiguration)) return false;

        SensorSimulationConfiguration that = (SensorSimulationConfiguration) o;

        if (exceptionProbability != null ? !exceptionProbability.equals(that.exceptionProbability) : that.exceptionProbability != null) return false;
        if (errorProbability != null ? !errorProbability.equals(that.errorProbability) : that.errorProbability != null) return false;
        if (permanentErrorProbability != null ? !permanentErrorProbability.equals(that.permanentErrorProbability) : that.permanentErrorProbability != null) return false;
        if (goodStart != null ? !goodStart.equals(that.goodStart) : that.goodStart != null) return false;
        if (goodEnd != null ? !goodEnd.equals(that.goodEnd) : that.goodEnd != null) return false;
        if (lowest != null ? !lowest.equals(that.lowest) : that.lowest != null) return false;
        if (highest != null ? !highest.equals(that.highest) : that.highest != null) return false;
        if (outageSeconds != null ? !outageSeconds.equals(that.outageSeconds) : that.outageSeconds != null) return false;
        return outageSecondsVariance != null ? outageSecondsVariance.equals(that.outageSecondsVariance) : that.outageSecondsVariance == null;

    }

    @Override
    public int hashCode() {
        int result = exceptionProbability != null ? exceptionProbability.hashCode() : 0;
        result = 31 * result + (errorProbability != null ? errorProbability.hashCode() : 0);
        result = 31 * result + (permanentErrorProbability != null ? permanentErrorProbability.hashCode() : 0);
        result = 31 * result + (goodStart != null ? goodStart.hashCode() : 0);
        result = 31 * result + (goodEnd != null ? goodEnd.hashCode() : 0);
        result = 31 * result + (lowest != null ? lowest.hashCode() : 0);
        result = 31 * result + (highest != null ? highest.hashCode() : 0);
        result = 31 * result + (outageSeconds != null ? outageSeconds.hashCode() : 0);
        result = 31 * result + (outageSecondsVariance != null ? outageSecondsVariance.hashCode() : 0);
        return result;
    }
}
