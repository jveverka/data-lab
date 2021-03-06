package itx.image.service.model.values;

import java.util.Optional;

public class JPEGComponent implements TagValue<JPEGComponent.Value> {

    private final Value value;
    private final Optional<String> unit;

    public JPEGComponent(Value value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.JPEG_COMPONENT;
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
        private final String name;
        private final int samplingFactorByte;
        private final int quantizationTableNumber;

        public Value(String name, int samplingFactorByte, int quantizationTableNumber) {
            this.name = name;
            this.samplingFactorByte = samplingFactorByte;
            this.quantizationTableNumber = quantizationTableNumber;
        }

        public String getName() {
            return name;
        }

        public int getSamplingFactorByte() {
            return samplingFactorByte;
        }

        public int getQuantizationTableNumber() {
            return quantizationTableNumber;
        }
    }

}
