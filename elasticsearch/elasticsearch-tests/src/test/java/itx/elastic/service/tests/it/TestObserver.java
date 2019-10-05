package itx.elastic.service.tests.it;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.elastic.service.tests.it.dto.EventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestObserver implements Observer<EventData> {

    private static final Logger LOG = LoggerFactory.getLogger(TestObserver.class);

    private final List<EventData> docs;
    private final List<Throwable> errors;
    private final CountDownLatch cl;

    private Disposable disposable;

    public TestObserver() {
        this.cl = new CountDownLatch(1);
        this.docs = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        LOG.info("onSubscribe");
        this.disposable = disposable;
    }

    @Override
    public void onNext(EventData eventData) {
        docs.add(eventData);
        LOG.info("onNext: {}", docs.size());
    }

    @Override
    public void onError(Throwable e) {
        LOG.info("onError: {}", e.getMessage());
        errors.add(e);
        cl.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete");
        cl.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return cl.await(timeout, unit);
    }

    public List<EventData> getDocs() {
        return docs;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public boolean isFinished() {
        return cl.getCount() == 0;
    }

}
