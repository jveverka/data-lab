package itx.image.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.image.service.ParsingUtils;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MetaData {

    private final Map<String, DirectoryInfo> directories;

    @JsonCreator
    public MetaData(@JsonProperty("directories") Collection<DirectoryInfo> directories) {
        this.directories = new HashMap<>();
        directories.forEach(d->{
            this.directories.put(d.getName(), d);
        });
    }

    public Collection<DirectoryInfo> getDirectories() {
        return directories.values();
    }

    public Optional<DirectoryInfo> directoryByName(String name) {
        return Optional.ofNullable(directories.get(name));
    }

    public Set<String> directoryNames() {
        return directories.keySet();
    }

    @JsonIgnore
    public <T> Optional<T> getValueByPath(Class<T> type, String directoryName, String tagName) {
        return ParsingUtils.getValueByPath(this, type, directoryName, tagName);
    }

    @JsonIgnore
    public Optional<Float> getFloatValueByPath(String directoryName, String tagName) {
        return ParsingUtils.getFloatValueByPath(this, directoryName, tagName);
    }

}
