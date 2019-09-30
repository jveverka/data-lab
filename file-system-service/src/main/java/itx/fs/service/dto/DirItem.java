package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

public class DirItem {

    private final Path path;
    private final BasicFileAttributes attributes;
    private final CheckSum checkSum;

    public DirItem(Path path, BasicFileAttributes attributes, CheckSum checkSum) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attributes);
        this.path = path;
        this.attributes = attributes;
        this.checkSum = checkSum;
    }

    public DirItem(Path path, BasicFileAttributes attributes) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attributes);
        this.path = path;
        this.attributes = attributes;
        this.checkSum = null;
    }

    public Path getPath() {
        return path;
    }

    public BasicFileAttributes getAttributes() {
        return attributes;
    }

    public Optional<CheckSum> getCheckSum() {
        return Optional.ofNullable(checkSum);
    }

}
