package itx.fs.service.client;

import itx.fs.service.dto.DirItem;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LoggingDataSubscriber implements Subscriber<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingDataSubscriber.class);

    private final CountDownLatch subscribed;

    private Subscription s;
    private CountDownLatch dataCounter;
    private CountDownLatch completed;
    private long totalCounter;

    public LoggingDataSubscriber() {
        subscribed = new CountDownLatch(1);
        completed = new CountDownLatch(1);
        totalCounter = 0;
    }

    @Override
    public void onSubscribe(Subscription s) {
        LOG.info("onSubscribe:");
        this.s = s;
        subscribed.countDown();
    }

    @Override
    public void onNext(DirItem dirItem) {
        totalCounter++;
        dataCounter.countDown();
        LOG.info("onNext: {} {}", dataCounter.getCount(), dirItem.getPath().getFileName().toString());
    }

    @Override
    public void onError(Throwable t) {
        LOG.error("onError:", t);
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete:");
        while (dataCounter.getCount() > 0) {
            dataCounter.countDown();
        }
        completed.countDown();
    }

    public boolean awaitSubscription(long timeout, TimeUnit unit) throws InterruptedException {
        return subscribed.await(timeout, unit);
    }

    public void request(int n) {
        LOG.info("request: {}", n);
        if (s != null) {
            dataCounter = new CountDownLatch(n);
            s.request(n);
        }
    }

    public boolean isCompleted() {
        return completed.getCount() == 0;
    }

    public boolean awaitData(long timeout, TimeUnit unit) throws InterruptedException {
        LOG.info("awaitData: {}", dataCounter.getCount());
        return dataCounter.await(timeout, unit);
    }

    public long getTotalCounter() {
        return totalCounter;
    }

}
