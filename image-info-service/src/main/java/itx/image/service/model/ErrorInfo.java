package itx.image.service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorInfo {

    private final String error;

    @JsonCreator
    public ErrorInfo(@JsonProperty("error") String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

}
