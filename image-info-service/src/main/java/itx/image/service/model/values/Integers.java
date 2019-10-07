package itx.image.service.model.values;

import java.util.Optional;

public class Integers implements TagValue<int[]> {

    private final int[] value;
    private final Optional<String> unit;

    public Integers(int[] value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.INTEGERS;
    }

    @Override
    public int[] getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
