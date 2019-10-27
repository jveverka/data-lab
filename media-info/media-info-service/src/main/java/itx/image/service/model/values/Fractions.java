package itx.image.service.model.values;

import java.util.Optional;

public class Fractions implements TagValue<Fraction.Value[]> {

    private final Fraction.Value[] value;
    private final Optional<String> unit;

    public Fractions(Fraction.Value[] value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.FRACTIONS;
    }

    @Override
    public Fraction.Value[] getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
