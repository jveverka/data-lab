package itx.image.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.image.service.model.values.TagValue;

public class TagInfo {

    private final String name;
    private final String description;
    private final TagValue value;

    @JsonCreator
    public TagInfo(@JsonProperty("name") String name,
                   @JsonProperty("description") String description,
                   @JsonProperty("value") TagValue value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TagValue getValue() {
        return value;
    }

}
