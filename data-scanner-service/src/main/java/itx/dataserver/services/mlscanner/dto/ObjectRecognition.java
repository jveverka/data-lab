package itx.dataserver.services.mlscanner.dto;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.ml.service.odyolov3tf2.http.client.dto.DetectedObject;

import java.util.List;

public class ObjectRecognition {

    private final FileInfoId id;
    private final String path;
    private final List<DetectedObject> objects;

    public ObjectRecognition(FileInfoId id, String path, List<DetectedObject> objects) {
        this.id = id;
        this.path = path;
        this.objects = objects;
    }

    public FileInfoId getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public List<DetectedObject> getObjects() {
        return objects;
    }

}
