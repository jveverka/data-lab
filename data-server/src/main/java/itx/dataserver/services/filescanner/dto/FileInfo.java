package itx.dataserver.services.filescanner.dto;

public class FileInfo {

    private final FileInfoId id;
    private final FileSystemInfo fileSystemInfo;
    private final MediaInfo mediaInfo;

    public FileInfo(FileInfoId id, FileSystemInfo fileSystemInfo, MediaInfo mediaInfo) {
        this.id = id;
        this.fileSystemInfo = fileSystemInfo;
        this.mediaInfo = mediaInfo;
    }

    public FileInfoId getId() {
        return id;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

}
