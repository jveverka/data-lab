package itx.image.service.model.values;

import java.util.Optional;

public class IntegerValue implements TagValue<Integer> {

    private final Integer value;
    private final Optional<String> unit;

    public IntegerValue(Integer value, Optional<String> unit) {
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
    public Optional<String> getUnit() {
        return unit;
    }

}
