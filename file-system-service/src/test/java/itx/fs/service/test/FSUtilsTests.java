package itx.fs.service.test;

import itx.fs.service.dto.CheckSum;
import itx.fs.service.fsaccess.FSUtils;
import itx.fs.service.test.mocks.FileDataReaderMock;
import itx.fs.service.test.mocks.TestEmitter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class FSUtilsTests {

    @DataProvider(name = "testChecksumProvider")
    public static Object[][] getVideoPaths() {
        return new Object[][]{
                { "datafile-001.txt", FSUtils.SHA256, "f70c5e847d0ea29088216d81d628df4b4f68f3ccabb2e4031c09cc4d129ae216" },
                { "datafile-002.txt", FSUtils.SHA256, "1eca1fc3f047185db7bcffe142ee167ea52fa498f7438287e461629a1a6cd18f" },
        };
    }

    @Test(dataProvider = "testChecksumProvider")
    public void testCalculateChecksum(String resource, String algorithm, String checksum) throws IOException, NoSuchAlgorithmException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        CheckSum checkSum = FSUtils.calculateChecksum(is, algorithm);
        Assert.assertNotNull(checkSum);
        Assert.assertEquals(checkSum.getAlgorithm(), algorithm);
        Assert.assertEquals(checkSum.getChecksum(), checksum);
    }
    
    @Test(enabled = false)
    public void testWalkDirectoryRecursively() throws IOException, InterruptedException {
        Path basePath = Paths.get("/not/existing/path");
        TestEmitter testEmitter = new TestEmitter();
        FileDataReaderMock fileDataReader = new FileDataReaderMock(basePath);
        FSUtils.walkDirectoryRecursively(basePath, testEmitter, fileDataReader);
        testEmitter.await();
        Assert.assertTrue(testEmitter.getStatus());
    }
    
}
