package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.image.service.ImageService;
import itx.image.service.model.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FsObserver implements Observer<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(FsObserver.class);

    private final ElasticSearchService elasticSearchService;
    private final ImageService imageService;
    private final CountDownLatch subscribed;
    private final CountDownLatch completed;
    private AtomicLong counter;

    public FsObserver(ElasticSearchService elasticSearchService, ImageService imageService) {
        this.subscribed = new CountDownLatch(1);
        this.completed = new CountDownLatch(1);
        this.counter = new AtomicLong(1);
        this.elasticSearchService = elasticSearchService;
        this.imageService = imageService;
    }

    @Override
    public void onSubscribe(Disposable d) {
        subscribed.countDown();
    }

    @Override
    public void onNext(DirItem dirItem) {
        try {
            LOG.info("onNext: {} {}", counter.getAndIncrement(), dirItem.getPath().toString());
            File file = dirItem.getPath().toFile();
            Optional<MetaData> metaData = this.imageService.getMetaData(new FileInputStream(file));
            FileInfo fileInfo = DataUtils.createFileInfo(dirItem, metaData);
            this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);
        } catch(Exception e) {
            LOG.info("Exception: {}", e.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        LOG.info("onError: {}", e.getMessage());
        completed.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete:");
        completed.countDown();
    }

    public boolean awaitSubscribed(long timeout, TimeUnit unit) throws InterruptedException {
        return subscribed.await(timeout, unit);
    }

    public boolean awaitCompleted(long timeout, TimeUnit unit) throws InterruptedException {
        return completed.await(timeout, unit);
    }

    public void awaitCompleted() throws InterruptedException {
        completed.await();
    }
}
