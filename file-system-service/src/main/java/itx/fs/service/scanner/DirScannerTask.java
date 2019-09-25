package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.dto.DirItem;

import java.nio.file.Path;

public class DirScannerTask implements Runnable, Cancellable, Disposable {

    private final FlowableEmitter<DirItem> emitter;
    private final Path rootPath;

    public DirScannerTask(FlowableEmitter<DirItem> emitter, Path rootPath) {
        this.emitter = emitter;
        this.rootPath = rootPath;
    }

    @Override
    public void run() {
        emitter.setCancellable(this);
        emitter.setDisposable(this);
        emitter.requested();
        emitter.onNext(new DirItem());
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
