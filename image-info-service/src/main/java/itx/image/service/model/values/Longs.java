package itx.image.service.model.values;

import java.util.Optional;

public class Longs implements TagValue<long[]> {

    private final long[] value;
    private final String unit;

    public Longs(long[] value) {
        this.value = value;
        this.unit = null;
    }

    public Longs(long[] value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
