package itx.image.service.model.values;

import java.util.Optional;

public class DoubleValue implements TagValue<Double> {

    private final Double value;
    private final Optional<String> unit;

    public DoubleValue(Double value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
