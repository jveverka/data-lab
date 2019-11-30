package itx.fs.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.fsaccess.FSUtils;
import itx.fs.service.test.mocks.FileDataReaderMock;
import itx.fs.service.test.mocks.FsItemMock;
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
    public static Object[][] getTestChecksumProvider() {
        return new Object[][]{
                { "datafile-000.txt", FSUtils.SHA256, "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" },
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
        Assert.assertEquals(checkSum.getValue(), checksum);
    }

    @DataProvider(name = "testFileSystemDataProvider")
    public static Object[][] getTestFileSystemDataProvider() {
        return new Object[][]{
                { "file-system-data-000.json", "/not/existing/path-00", Long.valueOf(0), Long.valueOf(3) },
                { "file-system-data-001.json", "/not/existing/path-01", Long.valueOf(3), Long.valueOf(12) },
                { "file-system-data-002.json", "/not/existing/path-02", Long.valueOf(3), Long.valueOf(0) },
                { "file-system-data-003.json", "/not/existing/path-03", Long.valueOf(0), Long.valueOf(0) },
        };
    }

    @Test(dataProvider = "testFileSystemDataProvider")
    public void testWalkDirectoryRecursively(String resource, String rootPath, Long dirCount, Long fileCount) throws IOException, InterruptedException {
        Path basePath = Paths.get(rootPath);
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        FsItemMock fsItems = objectMapper.readValue(is, FsItemMock.class);
        TestEmitter testEmitter = new TestEmitter();
        FileDataReaderMock fileDataReader = new FileDataReaderMock(basePath, fsItems);
        FSUtils.walkDirectoryRecursively(Paths.get(basePath.toString(), "dir-001"), testEmitter, fileDataReader);
        testEmitter.await();
        Assert.assertTrue(testEmitter.getStatus());
        Assert.assertEquals(testEmitter.getDirCount(), dirCount);
        Assert.assertEquals(testEmitter.getFileCount(), fileCount);
    }
    
}
