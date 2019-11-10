package itx.dataserver.services.filescanner;

public enum MetaDataType {

    JPEG ("jpeg"),
    MP4 ("mp4"),
    NA ("not-available");

    private String typeName;

    MetaDataType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

}
