package itx.dataserver.services.filescanner.dto.metadata;

public class GPS {

    private final Coordinates coordinates;
    private final int altitude;
    private final String timeStamp;
    private final String procesingMethod;
    private final String dateStamp;

    public GPS(Coordinates coordinates, int altitude, String timeStamp, String procesingMethod, String dateStamp) {
        this.coordinates = coordinates;
        this.altitude = altitude;
        this.timeStamp = timeStamp;
        this.procesingMethod = procesingMethod;
        this.dateStamp = dateStamp;
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

    public String getProcesingMethod() {
        return procesingMethod;
    }

    public String getDateStamp() {
        return dateStamp;
    }

}
