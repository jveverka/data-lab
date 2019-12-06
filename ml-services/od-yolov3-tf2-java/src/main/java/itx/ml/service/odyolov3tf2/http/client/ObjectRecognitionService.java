package itx.ml.service.odyolov3tf2.http.client;

import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * HTTP client for remote communication with Object Recognition Service.
 */
public interface ObjectRecognitionService {

    /**
     * Get version of this service.
     * @return
     */
    Version getVersion() throws IOException, InterruptedException;

    /**
     * Get object recognitions for image in {@link InputStream}.
     * @param is image {@link InputStream}, supported formats are JPG, JPEG and PNG.
     * @param fileName file name in muti part attachment.
     * @param mimeType JPG: 'image/jpeg'; PNG: image/png'
     * @return object recognitions results.
     */
    Result getResult(InputStream is, String fileName, String mimeType) throws IOException, InterruptedException;

    /**
     * Get object recognitions for image on local file system {@link Path}.
     * @param {@link Path} to image on local file system, supported image formats are JPG, JPEG and PNG.
     * @return object recognitions results.
     */
    Result getResult(Path path) throws IOException, InterruptedException;

}
