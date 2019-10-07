package itx.image.service.model.values;

import java.util.Optional;

public class LongValue implements TagValue<Long> {

    private final Long value;
    private final Optional<String> unit;

    public LongValue(Long value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.LONG;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
