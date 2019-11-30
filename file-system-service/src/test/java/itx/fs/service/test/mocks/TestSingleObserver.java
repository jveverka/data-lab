package itx.fs.service.test.mocks;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.fs.service.dto.DirItem;

import java.util.concurrent.CountDownLatch;

public class TestSingleObserver implements SingleObserver<DirItem> {

    private final CountDownLatch cl;

    private DirItem dirItem;
    private Disposable d;
    private Throwable e;

    public TestSingleObserver() {
        this.cl = new CountDownLatch(1);
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onSuccess(DirItem dirItem) {
        if (cl.getCount() <= 0) {
            throw new UnsupportedOperationException("onNext called after observer termination !");
        }
        this.dirItem = dirItem;
        this.cl.countDown();
    }

    @Override
    public void onError(Throwable e) {
        this.e = e;
        this.cl.countDown();
    }

    public void await() throws InterruptedException {
        cl.await();
    }

    public Disposable getDisposable() {
        return d;
    }

    public DirItem getDirItem() {
        return dirItem;
    }

    public Throwable getError() {
        return e;
    }
}
