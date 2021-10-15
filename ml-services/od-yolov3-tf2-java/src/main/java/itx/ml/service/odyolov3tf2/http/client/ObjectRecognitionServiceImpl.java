package itx.ml.service.odyolov3tf2.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.ml.service.odyolov3tf2.http.client.dto.PathRequest;
import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.InputStream;
import java.nio.file.Path;

public class ObjectRecognitionServiceImpl implements ObjectRecognitionService {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ERROR = "Error";
    private static final String URI_VERSION = "/version";
    private static final String URI_LOCAL_DETECT = "/local-detect";
    private static final String URI_UPLOAD_DETECT = "/upload-detect";
    private static final String APPLICATION_JSON = "application/json";
    private static final String IMAGE_JPEG = "image/jpeg";

    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ObjectRecognitionServiceImpl(String baseUrl) {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    public ObjectRecognitionServiceImpl(String baseUrl, OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    @Override
    public Version getVersion() throws ORException {
        try {
            Request request = new Request.Builder()
                    .get()
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .url(baseUrl + URI_VERSION)
                    .build();
            Response response = httpClient.newCall(request).execute();
            return objectMapper.readValue(response.body().string(), Version.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

    @Override
    public Result getResult(InputStream is, String fileName, String mimeType) throws ORException {
        try {
            Request request = new Request.Builder()
                    .post(RequestBody.create(is.readAllBytes(), MediaType.parse(IMAGE_JPEG)))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .url(baseUrl + URI_UPLOAD_DETECT)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.code() != 200) {
                throw new ORException("Http status: " + response.code());
            }
            return objectMapper.readValue(response.body().string(), Result.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

    @Override
    public Result getResult(Path path) throws ORException {
        try {
            PathRequest pathRequest = new PathRequest(path.toString());
            String jsonData = objectMapper.writeValueAsString(pathRequest);
            Request request = new Request.Builder()
                    .post(RequestBody.create(jsonData, MediaType.parse(APPLICATION_JSON)))
                    .header(CONTENT_TYPE, APPLICATION_JSON)
                    .url(baseUrl + URI_LOCAL_DETECT)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.code() != 200) {
                throw new ORException("Http status: " + response.code());
            }
            return objectMapper.readValue(response.body().string(), Result.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

}
