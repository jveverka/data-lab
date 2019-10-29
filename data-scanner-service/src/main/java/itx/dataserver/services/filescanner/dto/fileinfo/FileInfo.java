package itx.dataserver.services.filescanner.dto.fileinfo;

public class FileInfo {

    private final FileInfoId id;
    private final FileSystemInfo fileSystemInfo;

    public FileInfo(FileInfoId id, FileSystemInfo fileSystemInfo) {
        this.id = id;
        this.fileSystemInfo = fileSystemInfo;
    }

    public FileInfoId getId() {
        return id;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

}
