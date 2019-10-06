package itx.image.service.model.values;

import java.util.Optional;

public class LongValue implements TagValue<Long> {

    private final Long value;
    private final String unit;

    public LongValue(Long value) {
        this.value = value;
        this.unit = null;
    }

    public LongValue(Long value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
