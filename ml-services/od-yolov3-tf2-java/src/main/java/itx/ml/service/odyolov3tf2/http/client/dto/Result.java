package itx.ml.service.odyolov3tf2.http.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Result {

    private final String path;
    private final Float time;
    private final List<DetectedObject> objects;
    private final Boolean result;

    @JsonCreator
    public Result(@JsonProperty("path") String path,
                  @JsonProperty("time") Float time,
                  @JsonProperty("objects") List<DetectedObject> objects,
                  @JsonProperty("result") Boolean result) {
        this.path = path;
        this.time = time;
        this.objects = objects;
        this.result = result;
    }

    public String getPath() {
        return path;
    }

    public Float getTime() {
        return time;
    }

    public List<DetectedObject> getObjects() {
        return objects;
    }

    public Boolean getResult() {
        return result;
    }

}
