package itx.image.service.model.values;

import java.util.Optional;

public class Integers implements TagValue<int[]> {

    private final int[] value;
    private final String unit;

    public Integers(int[] value) {
        this.value = value;
        this.unit = null;
    }

    public Integers(int[] value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
