package itx.image.service.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MetaData {

    private final Map<String, DirectoryInfo> directories;

    public MetaData(Collection<DirectoryInfo> directories) {
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

}
