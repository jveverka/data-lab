package itx.ml.service.odyolov3tf2.http.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Version {

    private final String version;

    @JsonCreator
    public Version(@JsonProperty("version") String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}
