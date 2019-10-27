package itx.image.service.model.values;

import java.util.List;
import java.util.Optional;

public class ObjectList implements TagValue<List<Object>> {

    private final List<Object> value;
    private final Optional<String> unit;

    public ObjectList(List<Object> value, Optional<String> unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public Type getType() {
        return Type.OBJECT_LIST;
    }

    @Override
    public List<Object> getValue() {
        return value;
    }

    @Override
    public Optional<String> getUnit() {
        return unit;
    }

}
