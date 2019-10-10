package itx.dataserver.services.filescanner.dto.metadata;

public class Coordinates {

    private final float lon;
    private final float lat;

    public Coordinates(float lon, float lat) {
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
