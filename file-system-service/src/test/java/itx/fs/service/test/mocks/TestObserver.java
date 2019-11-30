package itx.fs.service.test.mocks;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.fs.service.dto.DirItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestObserver implements Observer<DirItem> {

    private final CountDownLatch cl;
    private final List<DirItem> dirItemList;

    private Disposable d;
    private Throwable e;

    public TestObserver() {
        this.cl = new CountDownLatch(1);
        this.dirItemList = new ArrayList<>();
        this.d = null;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }

    @Override
    public void onNext(DirItem dirItem) {
        if (cl.getCount() <= 0) {
            throw new UnsupportedOperationException("onNext called after observer termination !");
        }
        this.dirItemList.add(dirItem);
    }

    @Override
    public void onError(Throwable e) {
        this.e = e;
        this.cl.countDown();
    }

    @Override
    public void onComplete() {
        this.cl.countDown();
    }

    public void await() throws InterruptedException {
        cl.await();
    }

    public Disposable getDisposable() {
        return d;
    }

    public List<DirItem> getDirItems() {
        return dirItemList;
    }

    public Throwable getError() {
        return e;
    }

    public Long getDirCount() {
        return dirItemList.stream().filter(d-> d.getAttributes().isDirectory()).count();
    }

    public Long getFileCount() {
        return dirItemList.stream().filter(d-> d.getAttributes().isRegularFile()).count();
    }

}
