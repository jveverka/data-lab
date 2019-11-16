package itx.dataserver.services.filescanner.dto.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinates {

    private final float lon;
    private final float lat;

    @JsonCreator
    public Coordinates(@JsonProperty("lon") float lon,
                       @JsonProperty("lat") float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public float getLat() {
        return lat;
    }

}
