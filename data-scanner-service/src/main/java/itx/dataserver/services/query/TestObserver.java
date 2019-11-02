package itx.dataserver.services.query;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestObserver implements Observer<FileInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(TestObserver.class);

    private final CountDownLatch cl;
    private final AtomicInteger ac;

    public TestObserver() {
        cl = new CountDownLatch(1);
        ac = new AtomicInteger(0);
    }

    @Override
    public void onSubscribe(Disposable d) {
        LOG.info("onSubscribe");
    }

    @Override
    public void onNext(FileInfo fileInfo) {
        LOG.info("onNext: {} {}", ac.incrementAndGet(), fileInfo.getFileSystemInfo().getPath());
    }

    @Override
    public void onError(Throwable e) {
        LOG.info("onError");
        cl.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("done");
        cl.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cl.await(timeout, unit);
    }

    public int getDocumentCount() {
        return ac.get();
    }

}
