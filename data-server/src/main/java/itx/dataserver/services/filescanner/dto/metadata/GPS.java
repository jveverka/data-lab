package itx.dataserver.services.filescanner.dto.metadata;

public class GPS {

    private final Coordinates coordinates;
    private final int altitude;
    private final String timeStamp;
    private final String processingMethod;

    public GPS(Coordinates coordinates, int altitude, String timeStamp, String processingMethod) {
        this.coordinates = coordinates;
        this.altitude = altitude;
        this.timeStamp = timeStamp;
        this.processingMethod = processingMethod;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getAltitude() {
        return altitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getProcessingMethod() {
        return processingMethod;
    }

}
