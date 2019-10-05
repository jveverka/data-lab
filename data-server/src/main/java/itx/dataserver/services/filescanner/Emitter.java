package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Emitter implements FlowableEmitter<DirItem> {

    private final CountDownLatch cl;
    private final ElasticSearchService elasticSearchService;

    private Disposable disposable;
    private Cancellable cancellable;

    public Emitter(ElasticSearchService elasticSearchService) {
        this.cl = new CountDownLatch(1);
        this.elasticSearchService = elasticSearchService;
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
        //this.elasticSearchService.saveDocument(FileInfo.class, );
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
