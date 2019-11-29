package itx.fs.service.dto;

import itx.fs.service.fsaccess.FSUtils;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

public class DirItem {

    private final Path path;
    private final BasicFileAttributes attributes;
    private final CheckSum checkSum;
    private final Optional<String> extension;

    public DirItem(Path path, BasicFileAttributes attributes, CheckSum checkSum) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attributes);
        this.path = path;
        this.attributes = attributes;
        this.checkSum = checkSum;
        this.extension = FSUtils.getFileExtension(path, attributes.isRegularFile());
    }

    public DirItem(Path path, BasicFileAttributes attributes) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attributes);
        this.path = path;
        this.attributes = attributes;
        this.checkSum = null;
        this.extension = Optional.empty();
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

    public Optional<String> getExtension() {
        return extension;
    }

}
