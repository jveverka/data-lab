package itx.image.service.model;

import java.util.Collection;

public class DirectoryInfo {

    private final String name;
    private final Collection<TagInfo> tags;
    private final Collection<ErrorInfo> errors;

    public DirectoryInfo( String name, Collection<TagInfo> tags, Collection<ErrorInfo> errors) {
        this.name = name;
        this.tags = tags;
        this.errors = errors;
    }

    public String getName() {
        return name;
    }

    public Collection<TagInfo> getTags() {
        return tags;
    }

    public Collection<ErrorInfo> getErrors() {
        return errors;
    }

}
