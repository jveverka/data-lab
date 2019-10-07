package itx.image.service.model.values;

import java.util.Optional;

public class Longs implements TagValue<long[]> {

    private final long[] value;
    private final Optional<String> unit;

    public Longs(long[] value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.BYTES;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
