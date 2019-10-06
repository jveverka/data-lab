package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
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

public class Emitter implements FlowableEmitter<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(Emitter.class);

    private final CountDownLatch cl;
    private final ElasticSearchService elasticSearchService;
    private final ImageService imageService;

    private Disposable disposable;
    private Cancellable cancellable;

    public Emitter(ElasticSearchService elasticSearchService, ImageService imageService) {
        this.cl = new CountDownLatch(1);
        this.elasticSearchService = elasticSearchService;
        this.imageService = imageService;
    }

    @Override
    public void setDisposable(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void setCancellable(Cancellable c) {
        this.cancellable = c;
    }

    @Override
    public long requested() {
        return 0;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public FlowableEmitter<DirItem> serialize() {
        return null;
    }

    @Override
    public boolean tryOnError(Throwable t) {
        return false;
    }

    @Override
    public void onNext(DirItem value) {
        try {
            File file = value.getPath().toFile();
            Optional<MetaData> metaData = this.imageService.getMetaData(new FileInputStream(file));
            FileInfo fileInfo = DataUtils.createFileInfo(value, metaData);
            boolean result = this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);
        } catch(Exception e) {
            LOG.info("Exception: {}", e.getMessage());
        }
    }

    @Override
    public void onError(Throwable error) {
        cl.countDown();
    }

    @Override
    public void onComplete() {
        cl.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cl.await(timeout, unit);
    }

}
