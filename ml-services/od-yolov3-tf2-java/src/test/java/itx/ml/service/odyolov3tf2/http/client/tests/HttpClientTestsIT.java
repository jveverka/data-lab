package itx.ml.service.odyolov3tf2.http.client.tests;

import itx.ml.service.odyolov3tf2.http.client.ORException;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionService;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionServiceImpl;
import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

public class HttpClientTestsIT {

    private final ObjectRecognitionService objectRecognitionService;

    public HttpClientTestsIT() {
        this.objectRecognitionService = new ObjectRecognitionServiceImpl("http://127.0.0.1:5000");
    }

    @Test
    public void testVersion() throws ORException {
        Version version = objectRecognitionService.getVersion();
        Assert.assertNotNull(version);
        Assert.assertNotNull(version.getVersion());
    }

    @Test
    public void testResultUploadDetect() throws ORException {
        String fileName = "IMG_20180827_190350.jpg";
        String mimeType = "image/jpeg";
        InputStream is = this.getClass().getResourceAsStream("/media/IMG_20180827_190350.jpg");
        Result result = objectRecognitionService.getResult(is, fileName, mimeType);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getPath());
        Assert.assertNotNull(result.getTime());
        Assert.assertNotNull(result.getObjects());
        Assert.assertNotNull(result.getResult());
        Assert.assertNotNull(result.getMessage());
    }

}
