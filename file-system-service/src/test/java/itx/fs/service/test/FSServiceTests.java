package itx.fs.service.test;

import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.FSService;
import itx.fs.service.FSServiceImpl;
import itx.fs.service.FSUtils;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.test.mocks.DirScannerMock;
import itx.fs.service.test.mocks.SynchronousDataSubscriber;
import itx.fs.service.test.mocks.TestEmitter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FSServiceTests {
    
    @Test
    public void testFSService() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FSService fsService = new FSServiceImpl(executor, new DirScannerMock());
        Flowable<DirItem> rootScanner = fsService.scanDirectory(new DirQuery(Paths.get("/root")));
        SynchronousDataSubscriber synchronousDataSubscriber = new SynchronousDataSubscriber();
        rootScanner.subscribe(synchronousDataSubscriber);
        synchronousDataSubscriber.request(10);
        synchronousDataSubscriber.await(10, TimeUnit.SECONDS);

        Assert.assertTrue(synchronousDataSubscriber.getErrors().size() == 0);
        Assert.assertTrue(synchronousDataSubscriber.getResults().size() == 1);
        executor.shutdown();
    }

    @Test(enabled = false)
    public void testWalkDirectoryRecursively() throws IOException, InterruptedException {
        Path path = Paths.get("/datapool/juraj/Photos");
        TestEmitter testEmitter = new TestEmitter();
        FSUtils.walkDirectoryRecursively(path, testEmitter);
        testEmitter.await();
        Assert.assertTrue(testEmitter.getStatus());
    }
    
}
