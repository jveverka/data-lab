package itx.ml.service.odyolov3tf2.http.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DetectedObject {

    private final String classId;
    private final Float[] box;
    private final Float score;

    @JsonCreator
    public DetectedObject(@JsonProperty("classId") String classId,
                          @JsonProperty("box") Float[] box,
                          @JsonProperty("score") Float score) {
        this.classId = classId;
        this.box = box;
        this.score = score;
    }

    public String getClassId() {
        return classId;
    }

    public Float[] getBox() {
        return box;
    }

    public Float getScore() {
        return score;
    }

}
