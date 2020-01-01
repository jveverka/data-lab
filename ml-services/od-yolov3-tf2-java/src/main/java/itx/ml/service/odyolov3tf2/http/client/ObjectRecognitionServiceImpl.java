package itx.ml.service.odyolov3tf2.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.ml.service.odyolov3tf2.http.client.dto.PathRequest;
import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ObjectRecognitionServiceImpl implements ObjectRecognitionService {

    private static final String PROTOCOL_PREFIX = "http://";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ERROR = "Error";

    private final HttpClient httpClient;
    private final URI getVersionUri;
    private final URI getResultLocalDetect;
    private final URI getResultUploadDetect;
    private final ObjectMapper objectMapper;

    public ObjectRecognitionServiceImpl(InetSocketAddress address) {
        InetSocketAddress.createUnresolved("", 120);
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper();
        this.getVersionUri = URI.create(PROTOCOL_PREFIX  + address.getHostName() + ":" + address.getPort() + "/version");
        this.getResultLocalDetect = URI.create(PROTOCOL_PREFIX  + address.getHostName() + ":" + address.getPort() + "/local-detect");
        this.getResultUploadDetect = URI.create(PROTOCOL_PREFIX  + address.getHostName() + ":" + address.getPort() + "/upload-detect");
    }

    @Override
    public Version getVersion() throws ORException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header(CONTENT_TYPE, "application/json")
                    .uri(getVersionUri)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), Version.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

    @Override
    public Result getResult(InputStream is, String fileName, String mimeType) throws ORException {
        try {
            String boundary = "X-HTTP11CLIENT-SEPARATOR";
            HttpRequest request = HttpRequest.newBuilder()
                .POST(ofMimeMultipartData(is, fileName, mimeType, boundary))
                .header(CONTENT_TYPE, "multipart/form-data;boundary=" + boundary)
                .uri(getResultUploadDetect)
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ORException("Http status: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), Result.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

    @Override
    public Result getResult(Path path) throws ORException {
        try {
            PathRequest pathRequest = new PathRequest(path.toString());
            String jsonData = objectMapper.writeValueAsString(pathRequest);
            HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonData))
                .header(CONTENT_TYPE, "application/json")
                .uri(getResultLocalDetect)
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ORException("Http status: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), Result.class);
        } catch (Exception e) {
            throw new ORException(ERROR, e);
        }
    }

    private static HttpRequest.BodyPublisher ofMimeMultipartData(InputStream is, String fileName, String mimeType, String boundary) throws IOException {
        List<byte[]> byteArrays = new ArrayList<>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

        byteArrays.add(separator);

        byteArrays.add(("\"file\"; filename=\"" + fileName
                        + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(is.readAllBytes());
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));

        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

}
