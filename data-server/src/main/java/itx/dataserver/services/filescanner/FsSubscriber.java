package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.image.service.ImageService;
import itx.image.service.model.MetaData;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FsSubscriber implements Subscriber<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(FsSubscriber.class);

    private final CountDownLatch cl;
    private final CountDownLatch clSubscribed;
    private final ElasticSearchService elasticSearchService;
    private final ImageService imageService;

    private Subscription subscription;
    private long counter;
    private CountDownLatch clRequested;

    public FsSubscriber(ElasticSearchService elasticSearchService, ImageService imageService) {
        this.cl = new CountDownLatch(1);
        this.clSubscribed = new CountDownLatch(1);
        this.elasticSearchService = elasticSearchService;
        this.imageService = imageService;
        this.counter = 0;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.clSubscribed.countDown();
    }

    @Override
    public void onNext(DirItem dirItem) {
        try {
            LOG.info("onNext: {}:{}", counter, dirItem.getPath().toString());
            clRequested.countDown();
            File file = dirItem.getPath().toFile();
            Optional<MetaData> metaData = this.imageService.getMetaData(new FileInputStream(file));
            FileInfo fileInfo = DataUtils.createFileInfo(dirItem, metaData);
            this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);
            counter++;
        } catch(Exception e) {
            LOG.info("Exception: {}", e.getMessage());
        }
    }

    @Override
    public void onError(Throwable t) {
        LOG.info("onError: {}", t.getMessage());
        cl.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete:");
        while (clRequested.getCount() > 0) {
            clRequested.countDown();
        }
        cl.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cl.await(timeout, unit);
    }

    public boolean awaitSubscribed(long timeout, TimeUnit unit) throws InterruptedException {
        return clSubscribed.await(timeout, unit);
    }

    public boolean isComplete() {
        return cl.getCount() == 0;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void requestData(int n) {
        subscription.request(n);
        clRequested = new CountDownLatch(n);
    }

    public void awaitRequested() throws InterruptedException {
        clRequested.await();
    }

}
