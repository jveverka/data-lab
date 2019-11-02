package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.image.service.MediaService;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FsObserver implements Observer<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(FsObserver.class);

    private final ElasticSearchService elasticSearchService;
    private final MediaService mediaService;
    private final CountDownLatch subscribed;
    private final CountDownLatch completed;
    private AtomicLong counter;

    public FsObserver(ElasticSearchService elasticSearchService, MediaService mediaService) {
        this.subscribed = new CountDownLatch(1);
        this.completed = new CountDownLatch(1);
        this.counter = new AtomicLong(1);
        this.elasticSearchService = elasticSearchService;
        this.mediaService = mediaService;
    }

    @Override
    public void onSubscribe(Disposable d) {
        subscribed.countDown();
    }

    @Override
    public void onNext(DirItem dirItem) {
        File file = dirItem.getPath().toFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            LOG.info("onNext: {} {}", counter.getAndIncrement(), dirItem.getPath().toString());
            Optional<MetaData> metaData = this.mediaService.getMetaData(fis);
            FileInfo fileInfo = DataUtils.createFileInfo(dirItem);
            this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);

            if (metaData.isPresent()) {

                Optional<MetaDataInfo> metaDataInfo = DataUtils.createMetaDataInfo(fileInfo.getId(), metaData.get());

                if (metaDataInfo.isPresent()) {
                    try {
                        this.elasticSearchService.saveDocument(MetaDataInfo.class, metaDataInfo.get());
                    } catch (Exception e) {
                        String jsonData = ParsingUtils.writeAsJsonString(metaData.get());
                        String stackTrace = DataUtils.getStackTraceAsString(e);
                        UnmappedData unmappedData =
                                new UnmappedData(fileInfo.getId(), MetaDataInfo.class.getTypeName(), jsonData, dirItem.getPath().toString(), "ElasticSearch_write_failed", stackTrace);
                        this.elasticSearchService.saveDocument(UnmappedData.class, unmappedData);
                    }
                } else {
                    String jsonData = ParsingUtils.writeAsJsonString(metaData.get());
                    UnmappedData unmappedData =
                               new UnmappedData(fileInfo.getId(), MetaDataInfo.class.getTypeName(), jsonData, dirItem.getPath().toString(), "MetaDataInfo_mapping_failed", "");
                    this.elasticSearchService.saveDocument(UnmappedData.class, unmappedData);
                }

            } else {
                LOG.trace("MetaData not present for {}", dirItem.getPath().toString());
            }
        } catch(Exception e) {
            LOG.error("Exception: ", e);
            try {
                String id = UUID.randomUUID().toString();
                String jsonData = dirItem.getPath().toString();
                String stackTrace = DataUtils.getStackTraceAsString(e);
                UnmappedData unmappedData =
                        new UnmappedData(id, DirItem.class.getTypeName(), jsonData, dirItem.getPath().toString(), "DirItem_mapping_failed", stackTrace);
                this.elasticSearchService.saveDocument(UnmappedData.class, unmappedData);
            } catch (Exception ex) {
                LOG.error("ES logging Exception: ", e);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        LOG.info("onError: {}", e.getMessage());
        completed.countDown();
    }

    @Override
    public void onComplete() {
        LOG.info("onComplete:");
        completed.countDown();
    }

    public boolean awaitSubscribed(long timeout, TimeUnit unit) throws InterruptedException {
        return subscribed.await(timeout, unit);
    }

    public boolean awaitCompleted(long timeout, TimeUnit unit) throws InterruptedException {
        return completed.await(timeout, unit);
    }

    public void awaitCompleted() throws InterruptedException {
        completed.await();
    }
}
