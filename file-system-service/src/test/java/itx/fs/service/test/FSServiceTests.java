package itx.fs.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import itx.fs.service.FSService;
import itx.fs.service.FSServiceImpl;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.test.mocks.FileDataReaderMock;
import itx.fs.service.test.mocks.FsItemMock;
import itx.fs.service.test.mocks.TestObserver;
import itx.fs.service.test.mocks.TestSingleObserver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FSServiceTests {

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
    public void testScanDirectoryAsync(String resource, String rootPath, Long dirCount, Long fileCount) throws IOException, InterruptedException {
        Path basePath = Paths.get(rootPath);
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        FsItemMock fsItems = objectMapper.readValue(is, FsItemMock.class);
        FileDataReaderMock fileDataReader = new FileDataReaderMock(basePath, fsItems);
        Executor executor = Executors.newFixedThreadPool(4);
        DirQuery query = new DirQuery(Paths.get(basePath.toString(), "dir-001"), 1);

        FSService fsService = new FSServiceImpl(executor, fileDataReader);
        Observable<DirItem> dirItemObservable = fsService.scanDirectoryAsync(query);
        TestObserver testObserver = new TestObserver();
        dirItemObservable.subscribe(testObserver);
        testObserver.await();

        Assert.assertNotNull(testObserver.getDisposable());
        Assert.assertNull(testObserver.getError());
        Assert.assertEquals(testObserver.getDirCount(), dirCount);
        Assert.assertEquals(testObserver.getFileCount(), fileCount);
    }

    @DataProvider(name = "testFileSystemDataProviderNext")
    public static Object[][] getTestFileSystemDataProviderNext() {
        return new Object[][]{
                { "file-system-data-001.json", "/not/existing/path-01", "/not/existing/path-01/dir-001/file-001.txt", Boolean.FALSE },
                { "file-system-data-001.json", "/not/existing/path-02", "/not/existing/path-02/dir-001/dir-011/file-011.txt", Boolean.FALSE },
                { "file-system-data-001.json", "/not/existing/path-03", "/not/existing/path-03/dir-001/dir-011", Boolean.TRUE },
                { "file-system-data-001.json", "/not/existing/path-03", "/not/existing/path-03/dir-001", Boolean.TRUE },
        };
    }

    @Test(dataProvider = "testFileSystemDataProviderNext")
    public void testScanSingleFileOrDirectory(String resource, String rootPath, String scanPath, Boolean isDirectory) throws IOException, InterruptedException {
        Path basePath = Paths.get(rootPath);
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        FsItemMock fsItems = objectMapper.readValue(is, FsItemMock.class);
        FileDataReaderMock fileDataReader = new FileDataReaderMock(basePath, fsItems);
        Executor executor = Executors.newFixedThreadPool(4);

        FSService fsService = new FSServiceImpl(executor, fileDataReader);
        Single<DirItem> dirItemSingle = fsService.scanSingleFileOrDirectory(Path.of(scanPath));
        TestSingleObserver testSingleObserver = new TestSingleObserver();
        dirItemSingle.subscribe(testSingleObserver);
        testSingleObserver.await();

        Assert.assertNotNull(testSingleObserver.getDisposable());
        Assert.assertNull(testSingleObserver.getError());
        Assert.assertNotNull(testSingleObserver.getDirItem());
        Boolean isActualDirectory = testSingleObserver.getDirItem().getAttributes().isDirectory();
        Assert.assertEquals(isActualDirectory, isDirectory);
        Assert.assertNotNull(testSingleObserver.getDirItem().getCheckSum());
        Assert.assertNotNull(testSingleObserver.getDirItem().getPath());
        Assert.assertNotNull(testSingleObserver.getDirItem().getExtension());
    }

    @Test
    public void testConstructor() {
        Executor executor = Executors.newSingleThreadExecutor();
        FSService fsService = new FSServiceImpl(executor);
        Assert.assertNotNull(fsService);
    }

}
