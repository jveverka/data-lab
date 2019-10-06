package itx.image.service.model.values;

import java.util.Optional;

public class Fractions implements TagValue<Fraction.Value[]> {

    private final Fraction.Value[] value;
    private final String unit;

    public Fractions(Fraction.Value[] value) {
        this.value = value;
        this.unit = null;
    }

    public Fractions(Fraction.Value[] value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.FRACTION;
    }

    @Override
    public Fraction.Value[] getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
