package itx.fs.service.test.mocks;

import io.reactivex.rxjava3.core.Emitter;
import itx.fs.service.dto.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class TestEmitter implements Emitter<FileItem> {

    private static final Logger LOG = LoggerFactory.getLogger(TestEmitter.class);

    private final CountDownLatch cl;
    private long fileCounter;
    private long dirCounter;
    private boolean error;

    public TestEmitter() {
        this.cl = new CountDownLatch(1);
        this.fileCounter = 0;
        this.dirCounter = 0;
        this.error = false;
    }

    @Override
    public void onNext(FileItem value) {
        if (cl.getCount() <= 0) {
            throw new UnsupportedOperationException("onNext called after observer termination !");
        }
        if (value.getBasicFileAttributes().isDirectory()) {
            dirCounter++;
        } else {
            fileCounter++;
        }
    }

    @Override
    public void onError(Throwable error) {
        this.error = true;
        cl.countDown();
    }

    @Override
    public void onComplete() {
        cl.countDown();
    }

    public void await() throws InterruptedException {
        cl.await();
    }

    public boolean getStatus() {
        LOG.info("Dirs : {}", dirCounter);
        LOG.info("Files: {}", fileCounter);
        return !error;
    }

    public Long getFileCount() {
        return fileCounter;
    }

    public Long getDirCount() {
        return dirCounter;
    }

}
