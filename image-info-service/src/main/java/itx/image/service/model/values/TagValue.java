package itx.image.service.model.values;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import itx.image.service.model.converters.StringToTagValueConverter;

import java.util.Optional;

@JsonDeserialize(using = StringToTagValueConverter.class)
public interface TagValue<T> {

    Type getType();

    T getValue();

    Optional<String> getUnit();

}
