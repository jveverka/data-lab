package itx.fs.service;

import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.client.LoggingDataSubscriber;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FsMain {

    private static final Logger LOG = LoggerFactory.getLogger(FsMain.class);

    public static void main(String[] args) throws InterruptedException {
        LOG.info("File system Scan Started: {}", args[0]);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        FSService fsService = new FSServiceImpl(executor);

        DirQuery query = DirQuery.create(args[0]);
        Flowable<DirItem> dirItemFlowable = fsService.scanDirectory(query);

        LoggingDataSubscriber loggingDataSubscriber = new LoggingDataSubscriber();
        dirItemFlowable.subscribe(loggingDataSubscriber);
        loggingDataSubscriber.awaitSubscription(5, TimeUnit.SECONDS);

        while(!loggingDataSubscriber.isCompleted()) {
            loggingDataSubscriber.request(10);
            loggingDataSubscriber.awaitData(15, TimeUnit.SECONDS);
        }

        executor.shutdown();
        LOG.info("FS: done {}.", loggingDataSubscriber.getTotalCounter());
    }

}
