package itx.image.service.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DirectoryInfo {

    private final String name;
    private final Map<String, TagInfo> tags;
    private final Collection<ErrorInfo> errors;

    public DirectoryInfo( String name, Collection<TagInfo> tags, Collection<ErrorInfo> errors) {
        this.name = name;
        this.tags = new HashMap<>();
        tags.forEach(t->{
            this.tags.put(t.getName(), t);
        });
        this.errors = errors;
    }

    public String getName() {
        return name;
    }

    public Collection<TagInfo> getTags() {
        return tags.values();
    }

    public Optional<TagInfo> tagInfoByName(String name) {
        return Optional.ofNullable(tags.get(name));
    }

    public Set<String> tagInfoNames() {
        return tags.keySet();
    }

    public Collection<ErrorInfo> getErrors() {
        return errors;
    }

}
