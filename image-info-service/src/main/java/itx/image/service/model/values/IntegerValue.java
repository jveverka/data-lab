package itx.image.service.model.values;

import java.util.Optional;

public class IntegerValue implements TagValue<Integer> {

    private final Integer value;
    private final String unit;

    public IntegerValue(Integer value) {
        this.value = value;
        this.unit = null;
    }

    public IntegerValue(Integer value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
