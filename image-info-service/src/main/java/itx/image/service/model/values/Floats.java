package itx.image.service.model.values;

import java.util.Optional;

public class Floats implements TagValue<float[]> {

    private final float[] value;
    private final Optional<String> unit;

    public Floats(float[] value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.FLOATS;
    }

    @Override
    public float[] getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
