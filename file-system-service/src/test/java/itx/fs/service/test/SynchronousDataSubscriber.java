package itx.fs.service.test;

import itx.fs.service.dto.DirItem;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SynchronousDataSubscriber implements Subscriber<DirItem> {

    private Subscription s;
    private final List<DirItem> items;
    private final List<Throwable> errors;
    private final CountDownLatch cl;

    public SynchronousDataSubscriber() {
        items = new ArrayList<>();
        errors = new ArrayList<>();
        cl = new CountDownLatch(1);
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.s = s;
    }

    @Override
    public void onNext(DirItem dirItem) {
        items.add(dirItem);
    }

    @Override
    public void onError(Throwable t) {
        errors.add(t);
    }

    @Override
    public void onComplete() {
        cl.countDown();
    }

    public void request(long n) {
        if (s != null) {
            s.request(n);
        }
    }

    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        cl.await(timeout, unit);
    }

    public List<DirItem> getResults() {
        return items;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

}
