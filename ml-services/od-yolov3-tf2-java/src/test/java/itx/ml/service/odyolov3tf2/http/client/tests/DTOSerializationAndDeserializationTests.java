package itx.ml.service.odyolov3tf2.http.client.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.ml.service.odyolov3tf2.http.client.dto.PathRequest;
import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import itx.ml.service.odyolov3tf2.http.client.dto.Version;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

public class DTOSerializationAndDeserializationTests {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testVersionIO() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/version.json");
        Version version = objectMapper.readValue(is, Version.class);
        Assert.assertNotNull(version);
        Assert.assertNotNull(version.getVersion());
    }

    @Test
    public void testPathIO() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/path-request.json");
        PathRequest pathRequest = objectMapper.readValue(is, PathRequest.class);
        Assert.assertNotNull(pathRequest);
        Assert.assertNotNull(pathRequest.getPath());
    }

    @DataProvider(name = "testDataProvider")
    public static Object[][] getTestDataProvider() {
        return new Object[][]{
                { "/result-001.json" },
                { "/result-002.json" },
        };
    }

    @Test(dataProvider = "testDataProvider")
    public void testResultIO(String dataSource) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(dataSource);
        Result result = objectMapper.readValue(is, Result.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObjects());
        Assert.assertNotNull(result.getPath());
        Assert.assertNotNull(result.getTime());
        Assert.assertNotNull(result.getResult());
        Assert.assertNotNull(result.getMessage());
    }

}
