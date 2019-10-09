package itx.dataserver.services.filescanner.dto;

public class FileInfo {

    private final FileInfoId id;
    private final FileSystemInfo fileSystemInfo;
    private final FileMetaData metaData;

    public FileInfo(FileInfoId id, FileSystemInfo fileSystemInfo, FileMetaData metaData) {
        this.id = id;
        this.fileSystemInfo = fileSystemInfo;
        this.metaData = metaData;
    }

    public FileInfoId getId() {
        return id;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

    public FileMetaData getMetaData() {
        return metaData;
    }

}
