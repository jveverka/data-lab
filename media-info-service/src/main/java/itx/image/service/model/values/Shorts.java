package itx.image.service.model.values;

import java.util.Optional;

public class Shorts implements TagValue<short[]> {

    private final short[] value;
    private final Optional<String> unit;

    public Shorts(short[] value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.SHORTS;
    }

    @Override
    public short[] getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
