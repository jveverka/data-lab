package itx.ml.service.odyolov3tf2.http.client;

public class ORException extends Exception {

    public ORException(String message) {
        super(message);
    }

    public ORException(String message, Exception e) {
        super(message, e);
    }

}
