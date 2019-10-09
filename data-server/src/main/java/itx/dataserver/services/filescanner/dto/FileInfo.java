package itx.dataserver.services.filescanner.dto;

public class FileInfo {

    private final FileInfoId id;
    private final FileSystemInfo fileSystemInfo;
    private final MetaDataContainer metaData;
    private final ContentAnnotationContainer annotations;

    public FileInfo(FileInfoId id, FileSystemInfo fileSystemInfo, MetaDataContainer metaData, ContentAnnotationContainer annotations) {
        this.id = id;
        this.fileSystemInfo = fileSystemInfo;
        this.metaData = metaData;
        this.annotations = annotations;
    }

    public FileInfoId getId() {
        return id;
    }

    public FileSystemInfo getFileSystemInfo() {
        return fileSystemInfo;
    }

    public MetaDataContainer getMetaData() {
        return metaData;
    }

    public ContentAnnotationContainer getAnnotations() {
        return annotations;
    }

}
