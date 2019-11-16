package itx.dataserver.services.filescanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaData;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfo;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.image.service.MediaService;
import itx.image.service.model.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class FsObserver implements Observer<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(FsObserver.class);

    private final ElasticSearchService elasticSearchService;
    private final MediaService mediaService;
    private final String metaDataFileName;
    private final ObjectMapper objectMapper;
    private final Map<String, AnnotationMetaData> annotationCache;

    private final CountDownLatch subscribed;
    private final CountDownLatch completed;
    private final AtomicLong fileCounter;
    private final AtomicLong dirCounter;
    private final AtomicLong records;
    private final AtomicLong errors;
    private final AtomicLong annotations;

    public FsObserver(ElasticSearchService elasticSearchService, MediaService mediaService, String metaDataFileName) {
        this.elasticSearchService = elasticSearchService;
        this.mediaService = mediaService;
        this.metaDataFileName = metaDataFileName;
        this.annotationCache = new ConcurrentHashMap<>();
        this.objectMapper = new ObjectMapper();
        this.subscribed = new CountDownLatch(1);
        this.completed = new CountDownLatch(1);
        this.fileCounter = new AtomicLong(0);
        this.dirCounter = new AtomicLong(0);
        this.records = new AtomicLong(0);
        this.errors = new AtomicLong(0);
        this.annotations = new AtomicLong(0);
    }

    @Override
    public void onSubscribe(Disposable d) {
        subscribed.countDown();
    }

    @Override
    public void onNext(DirItem dirItem) {
        File file = dirItem.getPath().toFile();
        FileInfo fileInfo = null;

        if (file.isDirectory()) {
            try {

                Path annotationPath = Paths.get(dirItem.getPath().toString(), metaDataFileName);
                File annotationFile = annotationPath.toFile();
                if (annotationFile.isFile() && DataUtils.matchesAnnotationMetaDataFileName(annotationPath, metaDataFileName)) {
                    //load annotations into common cache pool
                    LOG.info("ANOT onNext:{}", annotationFile.getPath());
                    try (FileInputStream fis = new FileInputStream(annotationFile)) {
                        List<AnnotationMetaData> annotations = objectMapper.readValue(fis, new TypeReference<List<AnnotationMetaData>>() {});
                        for (AnnotationMetaData annotationMetaData: annotations) {
                            annotationMetaData = AnnotationMetaData.from(Paths.get(dirItem.getPath().toString(), annotationMetaData.getPath().toString()), annotationMetaData);
                            annotationCache.put(annotationMetaData.getPath().toString(), annotationMetaData);
                        }
                    } catch (Exception e) {
                        LOG.error("ANOT Exception: ", e);
                    }
                }

                LOG.info("DIR  onNext: {} {}", dirCounter.incrementAndGet(), dirItem.getPath());
                fileInfo = DataUtils.createFileInfo(dirItem);
                this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);
            } catch (Exception e) {
                errors.incrementAndGet();
                LOG.error("DIR Exception: ", e);
                try {
                    DataUtils.logESErrorDirMapping(elasticSearchService, dirItem.getPath(), e);
                } catch (Exception ex) {
                    LOG.error("DIR ES logging Exception: ", e);
                }
            }
        } else {
            if (file.isFile() && DataUtils.matchesAnnotationMetaDataFileName(dirItem.getPath(), metaDataFileName)) {
                // do not process data file with annotation data
                return;
            }
            try (FileInputStream fis = new FileInputStream(file)) {
                LOG.info("FILE onNext: {} {}", fileCounter.incrementAndGet(), dirItem.getPath());
                Optional<MetaData> metaData = this.mediaService.getMetaData(fis);
                fileInfo = DataUtils.createFileInfo(dirItem);
                this.elasticSearchService.saveDocument(FileInfo.class, fileInfo);
                this.records.incrementAndGet();

                if (metaData.isPresent()) {

                    MetaDataType metaDataType = DataUtils.getMetaDataType(metaData.get());
                    switch (metaDataType) {
                        case JPEG:
                            Optional<ImageMetaDataInfo> imageMetaDataInfo = DataUtils.createImageMetaDataInfo(fileInfo.getId(), metaData.get());
                            if (imageMetaDataInfo.isPresent()) {
                                try {
                                    this.elasticSearchService.saveDocument(ImageMetaDataInfo.class, imageMetaDataInfo.get());
                                } catch (Exception e) {
                                    errors.incrementAndGet();
                                    DataUtils.logESError(elasticSearchService, ImageMetaDataInfo.class, fileInfo.getId(), metaData.get(), e, dirItem.getPath(), "ElasticSearch_ImageMetaDataInfo_write_failed");
                                }
                            } else {
                                errors.incrementAndGet();
                                DataUtils.logESError(elasticSearchService, ImageMetaDataInfo.class, fileInfo.getId(), metaData.get(), dirItem.getPath(), "ImageMetaDataInfo_mapping_failed");
                            }
                            break;
                        case MP4:
                            Optional<VideoMetaDataInfo> videoMetaDataInfo = DataUtils.createVideoMetaDataInfo(fileInfo.getId(), metaData.get());
                            if (videoMetaDataInfo.isPresent()) {
                                try {
                                    this.elasticSearchService.saveDocument(VideoMetaDataInfo.class, videoMetaDataInfo.get());
                                } catch (Exception e) {
                                    errors.incrementAndGet();
                                    DataUtils.logESError(elasticSearchService, VideoMetaDataInfo.class, fileInfo.getId(), metaData.get(), e, dirItem.getPath(), "ElasticSearch_VideoMetaDataInfo_write_failed");
                                }
                            } else {
                                errors.incrementAndGet();
                                DataUtils.logESError(elasticSearchService, VideoMetaDataInfo.class, fileInfo.getId(), metaData.get(), dirItem.getPath(), "VideoMetaDataInfo_mapping_failed");
                            }
                            break;
                        case NA:
                            errors.incrementAndGet();
                            DataUtils.logESError(elasticSearchService, MetaDataType.class, fileInfo.getId(), metaData.get(), dirItem.getPath(), "MetaDataInfo_mapping_not_supported");
                            break;
                        default:
                            throw new UnsupportedOperationException("Unsupported MetaData type: " + metaDataType.getTypeName());
                    }
                } else {
                    LOG.trace("MetaData not present for {}", dirItem.getPath().toString());
                }

            } catch (Exception e) {
                LOG.error("Exception: ", e);
                try {
                    errors.incrementAndGet();
                    DataUtils.logESErrorDirMapping(elasticSearchService, dirItem.getPath(), e);
                } catch (Exception ex) {
                    LOG.error("ES logging Exception: ", e);
                }
            }
        }

        // Save annotation data
        if (fileInfo != null) {
            AnnotationMetaData annotation = annotationCache.remove(fileInfo.getFileSystemInfo().getPath());
            if (annotation != null) {
                annotation = AnnotationMetaData.from(fileInfo.getId(), annotation);
                try {
                    this.elasticSearchService.saveDocument(AnnotationMetaData.class, annotation);
                    annotations.incrementAndGet();
                } catch (IOException e) {
                    LOG.error("ANOT Exception: ", e);
                    errors.incrementAndGet();
                    try {
                        DataUtils.logESErrorDirMapping(elasticSearchService, dirItem.getPath(), e);
                    } catch (IOException ex) {
                        LOG.error("ES logging Exception: ", e);
                    }
                }
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

    public long getRecords() {
        return records.get();
    }

    public long getDirCounter() {
        return dirCounter.get();
    }

    public long getErrors() {
        return errors.get();
    }

    public long getAnnotations() {
        return annotations.get();
    }
}
