package itx.ml.service.odyolov3tf2.http.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PathRequest {

    private final String path;

    @JsonCreator
    public PathRequest(@JsonProperty("path") String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
