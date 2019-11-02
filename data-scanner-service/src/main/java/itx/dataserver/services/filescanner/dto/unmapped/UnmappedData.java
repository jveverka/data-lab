package itx.dataserver.services.filescanner.dto.unmapped;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;

public class UnmappedData {

    private final FileInfoId id;
    private final String type;
    private final String jsonData;
    private final String filePath;
    private final String reason;
    private final String stackTrace;

    @JsonCreator
    public UnmappedData(@JsonProperty("fileInfoId") String id,
                        @JsonProperty("type") String type,
                        @JsonProperty("jsonData") String jsonData,
                        @JsonProperty("filePath") String filePath,
                        @JsonProperty("reason") String reason,
                        @JsonProperty("stackTrace") String stackTrace) {
        this.id = new FileInfoId(id);
        this.type = type;
        this.jsonData = jsonData;
        this.filePath = filePath;
        this.reason = reason;
        this.stackTrace = stackTrace;
    }

    public UnmappedData(FileInfoId id, String type, String jsonData, String filePath, String reason, String stackTrace) {
        this.id = id;
        this.type = type;
        this.jsonData = jsonData;
        this.filePath = filePath;
        this.reason = reason;
        this.stackTrace = stackTrace;
    }

    /**
     * {@link FileInfoId} of file which mapping has failed.
     * @return
     */
    public FileInfoId getId() {
        return id;
    }

    /**
     * The type of data which mapping has failed.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * JSON data serialized.
     * @return
     */
    public String getJsonData() {
        return jsonData;
    }

    /**
     * Original file path, source of the data.
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Reason why mapping has failed.
     * @return
     */
    public String getReason() {
        return reason;
    }

    /**
     * Pretty printed stacktrace.
     * @return
     */
    public String getStackTrace() {
        return stackTrace;
    }

}
