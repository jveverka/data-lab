package itx.dataserver.services.filescanner.dto;

public class FileInfo {

    private final FileInfoId id;
    private final FileSystemInfo fileSystemInfo;
    private final MetaData metaData;

    public FileInfo(FileInfoId id, FileSystemInfo fileSystemInfo, MetaData metaData) {
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

    public MetaData getMetaData() {
        return metaData;
    }

}
