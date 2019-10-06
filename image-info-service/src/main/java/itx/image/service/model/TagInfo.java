package itx.image.service.model;

import itx.image.service.model.values.TagValue;

public class TagInfo {

    private final String name;
    private final String description;
    private final TagValue value;

    public TagInfo(String name, String description, TagValue value) {
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
