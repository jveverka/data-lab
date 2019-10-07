package itx.image.service.model.values;

import java.util.Optional;

public class StringValue implements TagValue<String> {

    private final String value;
    private final Optional<String> unit;

    public StringValue(String value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
