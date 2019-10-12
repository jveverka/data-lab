package itx.image.service.model.values;

import java.util.Date;
import java.util.Optional;

public class DateValue implements TagValue<Date> {

    private final Date value;
    private final Optional<String> unit;

    public DateValue(Date value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.DATE;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
