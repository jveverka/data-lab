package itx.elastic.service;

import io.reactivex.rxjava3.disposables.Disposable;

public class DisposableNoop implements Disposable {

    private boolean disposed = false;

    @Override
    public void dispose() {
        this.disposed = true;
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

}
