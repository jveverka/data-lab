package itx.image.service.model.values;

import java.util.Optional;

public class Fraction implements TagValue<Fraction.Value> {

    private final Value value;
    private final Optional<String> unit;

    public Fraction(Value value, Optional<String> unit) {
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
    public Optional<String> getUnit() {
        return unit;
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
