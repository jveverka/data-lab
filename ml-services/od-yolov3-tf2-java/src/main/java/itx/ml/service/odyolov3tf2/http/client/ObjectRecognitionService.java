package itx.ml.service.odyolov3tf2.http.client;

import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;

import java.io.InputStream;
import java.nio.file.Path;

public interface ObjectRecognitionService {

    Version getVersion();

    Result getResult(InputStream is);

    Result getResult(Path path);

}
