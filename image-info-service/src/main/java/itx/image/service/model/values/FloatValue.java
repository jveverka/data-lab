package itx.image.service.model.values;

import java.util.Optional;

public class FloatValue implements TagValue<Float> {

    private final Float value;
    private final Optional<String> unit;

    public FloatValue(Float value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
