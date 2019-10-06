package itx.image.service.model.values;

import java.util.Optional;

public class Bytes implements TagValue<byte[]> {

    private final byte[] value;
    private final String unit;

    public Bytes(byte[] value) {
        this.value = value;
        this.unit = null;
    }

    public Bytes(byte[] value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.BYTES;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public Optional<String> unitName() {
        return Optional.ofNullable(unit);
    }

}
