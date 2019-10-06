package itx.image.service.model.values;

import java.util.Optional;

public class Shorts implements TagValue<short[]> {

    private final short[] value;
    private final String unit;

    public Shorts(short[] value) {
        this.value = value;
        this.unit = null;
    }

    public Shorts(short[] value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
