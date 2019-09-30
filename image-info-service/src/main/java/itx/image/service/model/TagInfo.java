package itx.image.service.model;

public class TagInfo {

    private final String name;
    private final String description;
    private final int type;

    public TagInfo(String name, String description, int type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getType() {
        return type;
    }

}
