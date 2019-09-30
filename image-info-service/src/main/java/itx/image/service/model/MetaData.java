package itx.image.service.model;

import java.util.Collection;

public class MetaData {

    private final Collection<DirectoryInfo> directories;

    public MetaData(Collection<DirectoryInfo> directories) {
        this.directories = directories;
    }

    public Collection<DirectoryInfo> getDirectories() {
        return directories;
    }

}
