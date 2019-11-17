package itx.fs.service.test;

import itx.fs.service.FSUtils;
import itx.fs.service.test.mocks.TestEmitter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FSServiceTests {
    
    @Test(enabled = false)
    public void testWalkDirectoryRecursively() throws IOException, InterruptedException {
        Path path = Paths.get("/datapool/juraj/Photos");
        TestEmitter testEmitter = new TestEmitter();
        FSUtils.walkDirectoryRecursively(path, testEmitter);
        testEmitter.await();
        Assert.assertTrue(testEmitter.getStatus());
    }
    
}
