package itx.image.service.model.values;

import java.util.Optional;

public class BooleanValue implements TagValue<Boolean> {

    private final Boolean value;
    private final String unit;

    public BooleanValue(Boolean value) {
        this.value = value;
        this.unit = null;
    }

    public BooleanValue(Boolean value, String unit) {
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
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
