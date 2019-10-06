package itx.dataserver.services.filescanner.dto;

import itx.fs.service.dto.CheckSum;

import java.nio.file.attribute.FileTime;
import java.util.Optional;

public class FileSystemInfo {

    private final String path;
    private final Optional<CheckSum> checksum;
    private final FileTime creationTime;
    private final FileTime lastModifiedTime;
    private final FileTime lastAccessTime;
    private final FileType type;
    private final long size;


    public FileSystemInfo(String path, Optional<CheckSum> checksum, FileTime creationTime, FileTime lastModifiedTime, FileTime lastAccessTime, FileType type, long size) {
        this.path = path;
        this.checksum = checksum;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
        this.lastAccessTime = lastAccessTime;
        this.type = type;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public Optional<CheckSum> getChecksum() {
        return checksum;
    }

    public FileTime getCreationTime() {
        return creationTime;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public FileTime getLastAccessTime() {
        return lastAccessTime;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

}
