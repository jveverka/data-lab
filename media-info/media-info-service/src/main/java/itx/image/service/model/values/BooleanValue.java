package itx.image.service.model.values;

import java.util.Optional;

public class BooleanValue implements TagValue<Boolean> {

    private final Boolean value;
    private final Optional<String> unit;

    public BooleanValue(Boolean value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
