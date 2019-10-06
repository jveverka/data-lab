package itx.image.service.model.values;

import java.util.Optional;

public class DoubleValue implements TagValue<Double> {

    private final Double value;
    private final String unit;

    public DoubleValue(Double value) {
        this.value = value;
        this.unit = null;
    }

    public DoubleValue(Double value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
