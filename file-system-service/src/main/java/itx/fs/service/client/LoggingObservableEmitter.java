package itx.fs.service.client;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.dto.DirItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LoggingObservableEmitter implements ObservableEmitter<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingObservableEmitter.class);

    private CountDownLatch completed;

    public LoggingObservableEmitter() {
        completed = new CountDownLatch(1);
    }

    @Override
    public void setDisposable(Disposable d) {
        LOG.info("setDisposable");
    }

    @Override
    public void setCancellable(Cancellable c) {
        LOG.info("setCancellable");
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public ObservableEmitter<DirItem> serialize() {
        return null;
    }

    @Override
    public boolean tryOnError(Throwable t) {
        return false;
    }

    @Override
    public void onNext(DirItem value) {
        LOG.info("onNext");
    }

    @Override
    public void onError(Throwable error) {
        LOG.info("onError");
        completed.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete");
        completed.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return completed.await(timeout, unit);
    }

}
