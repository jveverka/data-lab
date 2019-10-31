package itx.dataserver.services.filescanner.dto.unmapped;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;

public class UnmappedData {

    private final FileInfoId id;
    private final String type;
    private final String jsonData;
    private final String filePath;

    public UnmappedData(FileInfoId id, String type, String jsonData, String filePath) {
        this.id = id;
        this.type = type;
        this.jsonData = jsonData;
        this.filePath = filePath;
    }

    public FileInfoId getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getJsonData() {
        return jsonData;
    }

    public String getFilePath() {
        return filePath;
    }

}
