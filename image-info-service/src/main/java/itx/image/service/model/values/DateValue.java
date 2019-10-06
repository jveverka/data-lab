package itx.image.service.model.values;

import java.util.Date;
import java.util.Optional;

public class DateValue implements TagValue<Date> {

    private final Date value;
    private final String unit;

    public DateValue(Date value) {
        this.value = value;
        this.unit = null;
    }

    public DateValue(Date value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
