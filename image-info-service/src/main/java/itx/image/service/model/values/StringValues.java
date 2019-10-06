package itx.image.service.model.values;

import java.util.Optional;

public class StringValues implements TagValue<String[]> {

    private final String[] value;
    private final String unit;

    public StringValues(String[] value) {
        this.value = value;
        this.unit = null;
    }

    public StringValues(String[] value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.STRINGS;
    }

    @Override
    public String[] getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
