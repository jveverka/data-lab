package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileItem {

    private final Path path;
    private final BasicFileAttributes basicFileAttributes;

    public FileItem(Path path, BasicFileAttributes basicFileAttributes) {
        this.path = path;
        this.basicFileAttributes = basicFileAttributes;
    }

    public Path getPath() {
        return path;
    }

    public BasicFileAttributes getBasicFileAttributes() {
        return basicFileAttributes;
    }

}
