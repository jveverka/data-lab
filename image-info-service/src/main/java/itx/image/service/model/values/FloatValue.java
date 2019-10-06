package itx.image.service.model.values;

import java.util.Optional;

public class FloatValue implements TagValue<Float> {

    private final Float value;
    private final String unit;

    public FloatValue(Float value) {
        this.value = value;
        this.unit = null;
    }

    public FloatValue(Float value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
