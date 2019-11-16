package itx.dataserver.services.filescanner.query;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchObserver implements Observer<FileInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchObserver.class);

    private final CountDownLatch cl;
    private final AtomicInteger ac;
    private final List<FileInfoId> documentIds;

    public SearchObserver() {
        this.cl = new CountDownLatch(1);
        this.ac = new AtomicInteger(0);
        this.documentIds = new ArrayList<>();
    }

    @Override
    public void onSubscribe(Disposable d) {
        LOG.info("onSubscribe");
    }

    @Override
    public void onNext(FileInfo fileInfo) {
        LOG.info("onNext: {} {}", ac.incrementAndGet(), fileInfo.getFileSystemInfo().getPath());
        documentIds.add(fileInfo.getId());
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

    public List<FileInfoId> getDocumentIds() {
        return documentIds;
    }

}
