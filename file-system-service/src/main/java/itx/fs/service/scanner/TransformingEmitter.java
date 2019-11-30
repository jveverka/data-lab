package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.FileItem;
import itx.fs.service.fsaccess.FileDataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransformingEmitter implements ObservableEmitter<FileItem>, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(TransformingEmitter.class);

    private final ObservableEmitter<DirItem> observableEmitter;
    private final ExecutorService executorService;
    private final CountDownLatch cl;
    private final FileDataReader fileDataReader;

    public TransformingEmitter(ObservableEmitter<DirItem> observableEmitter, int executorSize, FileDataReader fileDataReader) {
        this.observableEmitter = observableEmitter;
        this.executorService = Executors.newFixedThreadPool(executorSize);
        this.cl = new CountDownLatch(1);
        this.fileDataReader = fileDataReader;
    }

    @Override
    public void setDisposable(Disposable d) {
        this.observableEmitter.setDisposable(d);
    }

    @Override
    public void setCancellable(Cancellable c) {
        this.observableEmitter.setCancellable(c);
    }

    @Override
    public boolean isDisposed() {
        return this.observableEmitter.isDisposed();
    }

    @Override
    public ObservableEmitter<FileItem> serialize() {
        return null;
    }

    @Override
    public boolean tryOnError(Throwable t) {
        return this.observableEmitter.tryOnError(t);
    }

    @Override
    public void onNext(FileItem value) {
        executorService.submit(() -> {
            if (value.getBasicFileAttributes().isRegularFile()) {
                try {
                    CheckSum checkSum = fileDataReader.calculateSha256Checksum(value.getPath());
                    observableEmitter.onNext(new DirItem(value.getPath(), value.getBasicFileAttributes(), checkSum));
                } catch (NoSuchAlgorithmException e) {
                    observableEmitter.onError(e);
                    LOG.error("Checksum exception: ", e);
                } catch (IOException e) {
                    observableEmitter.onError(e);
                    LOG.error("IOException exception: ", e);
                }
            } else {
                observableEmitter.onNext(new DirItem(value.getPath(), value.getBasicFileAttributes()));
            }
        });
    }

    @Override
    public void onError(Throwable error) {
        LOG.info("onError");
        closeAndWait();
        this.observableEmitter.onError(error);
        cl.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete");
        closeAndWait();
        this.observableEmitter.onComplete();
        cl.countDown();
    }

    @Override
    public void close() throws InterruptedException {
        this.executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
        LOG.info("closed.");
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cl.await(timeout, unit);
    }

    private void closeAndWait() {
        try {
            close();
        } catch (InterruptedException e) {
            LOG.error("Close error: {}", e);
        }
    }

}
