package itx.image.service.model.values;

import java.util.Optional;

public interface TagValue<T> {

    Type getType();

    T getValue();

    Optional<String> getUnit();

}
