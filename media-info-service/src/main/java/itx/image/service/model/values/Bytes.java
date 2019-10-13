package itx.image.service.model.values;

import java.util.Optional;

public class Bytes implements TagValue<byte[]> {

    private final byte[] value;
    private final Optional<String> unit;

    public Bytes(byte[] value, Optional<String> unit) {
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
    public Optional<String> getUnit() {
        return unit;
    }

}
