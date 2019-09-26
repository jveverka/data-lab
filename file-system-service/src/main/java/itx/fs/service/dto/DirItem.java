package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class DirItem {

    private final Path path;
    private final BasicFileAttributes attributes;

    public DirItem(Path path, BasicFileAttributes attributes) {
        this.path = path;
        this.attributes = attributes;
    }

    public Path getPath() {
        return path;
    }

    public BasicFileAttributes getAttributes() {
        return attributes;
    }

}
