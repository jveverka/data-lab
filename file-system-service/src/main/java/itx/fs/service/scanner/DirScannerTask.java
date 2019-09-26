package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;

import java.io.IOException;


public class DirScannerTask implements Runnable, Cancellable, Disposable {

    private final FlowableEmitter<DirItem> emitter;
    private final DirQuery query;
    private final DirScanner dirScanner;

    public DirScannerTask(FlowableEmitter<DirItem> emitter, DirQuery query, DirScanner dirScanner) {
        this.emitter = emitter;
        this.query = query;
        this.dirScanner = dirScanner;
    }

    @Override
    public void run() {
        emitter.setCancellable(this);
        emitter.setDisposable(this);
        try {
            dirScanner.scanDirectory(emitter, query);
        } catch (IOException e) {
            emitter.onError(e);
        }
        emitter.onComplete();
    }

    @Override
    public void cancel() throws Throwable {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDisposed() {
        return false;
    }

}
