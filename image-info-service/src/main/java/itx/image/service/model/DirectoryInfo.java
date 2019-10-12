package itx.image.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DirectoryInfo {

    private final String name;
    private final Map<String, TagInfo> tags;
    private final Collection<ErrorInfo> errors;

    @JsonCreator
    public DirectoryInfo(@JsonProperty("name") String name,
                         @JsonProperty("tags") Collection<TagInfo> tags,
                         @JsonProperty("errors") Collection<ErrorInfo> errors) {
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
