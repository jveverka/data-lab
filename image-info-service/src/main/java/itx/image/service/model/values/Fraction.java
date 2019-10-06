package itx.image.service.model.values;

import java.util.Optional;

public class Fraction implements TagValue<Fraction.Value> {

    private final Value value;
    private final String unit;

    public Fraction(Value value) {
        this.value = value;
        this.unit = null;
    }

    public Fraction(Value value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.FRACTION;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

    public static class Value {

        private final long numerator;
        private final long denominator;

        public Value(long numerator, long denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        public long getNumerator() {
            return numerator;
        }

        public long getDenominator() {
            return denominator;
        }

        public float getFloatValue() {
            return numerator / ((float) denominator);
        }

    }

}
