package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.Observable;
import itx.dataserver.services.filescanner.dto.ScanRequest;
import itx.dataserver.services.filescanner.dto.ScanResponse;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoDataTransformer;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaData;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaDataTransformer;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfoTransformer;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfoTransformer;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedDataTransformer;
import itx.dataserver.services.filescanner.query.SearchObserver;
import itx.dataserver.services.mlscanner.MlScannerService;
import itx.dataserver.services.mlscanner.MlScannerServiceImpl;
import itx.dataserver.services.mlscanner.dto.ObjectRecognition;
import itx.dataserver.services.mlscanner.dto.ObjectRecognitionDataTransformer;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.fs.service.FSService;
import itx.fs.service.FSServiceImpl;
import itx.fs.service.dto.DirItem;
import itx.image.service.MediaService;
import itx.image.service.MediaServiceImpl;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionService;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionServiceImpl;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileScannerServiceImpl implements FileScannerService {

    private static final Logger LOG = LoggerFactory.getLogger(FileScannerServiceImpl.class);

    private final FSService dirScanner;
    private final ElasticSearchService elasticSearchService;
    private final MediaService mediaService;
    private final ExecutorService executorService;
    private final MlScannerService mlScannerService;
    private final ExecutorService mlExecutorService;

    public FileScannerServiceImpl(ClientConfig config, String mlBaseUrl, int executorSize) {
        LOG.info("FileScannerService: initializing ...");
        this.executorService = Executors.newFixedThreadPool(executorSize);
        this.dirScanner = new FSServiceImpl(executorService);
        this.elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        ImageMetaDataInfoTransformer imageMetaDataInfoTransformer = new ImageMetaDataInfoTransformer();
        VideoMetaDataInfoTransformer videoMetaDataInfoTransformer = new VideoMetaDataInfoTransformer();
        UnmappedDataTransformer unmappedDataTransformer = new UnmappedDataTransformer();
        AnnotationMetaDataTransformer annotationMetaDataTransformer = new AnnotationMetaDataTransformer();
        ObjectRecognitionDataTransformer objectRecognitionDataTransformer = new ObjectRecognitionDataTransformer();
        this.elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
        this.elasticSearchService.registerDataTransformer(ImageMetaDataInfo.class, imageMetaDataInfoTransformer);
        this.elasticSearchService.registerDataTransformer(VideoMetaDataInfo.class, videoMetaDataInfoTransformer);
        this.elasticSearchService.registerDataTransformer(UnmappedData.class, unmappedDataTransformer);
        this.elasticSearchService.registerDataTransformer(AnnotationMetaData.class, annotationMetaDataTransformer);
        this.elasticSearchService.registerDataTransformer(ObjectRecognition.class, objectRecognitionDataTransformer);
        this.mediaService = new MediaServiceImpl();
        this.mlExecutorService = Executors.newSingleThreadExecutor();
        ObjectRecognitionService objectRecognitionService = new ObjectRecognitionServiceImpl(mlBaseUrl);
        this.mlScannerService = new MlScannerServiceImpl(mlExecutorService, elasticSearchService, objectRecognitionService);
    }

    @Override
    public void initIndices() {
        LOG.info("deleting indices ...");
        deleteIndex(FileInfo.class);
        deleteIndex(ImageMetaDataInfo.class);
        deleteIndex(VideoMetaDataInfo.class);
        deleteIndex(UnmappedData.class);
        deleteIndex(AnnotationMetaData.class);
        deleteIndex(ObjectRecognition.class);
        LOG.info("creating indices ...");
        createIndex(FileInfo.class);
        createIndex(ImageMetaDataInfo.class);
        createIndex(VideoMetaDataInfo.class);
        createIndex(UnmappedData.class);
        createIndex(AnnotationMetaData.class);
        createIndex(ObjectRecognition.class);
        LOG.info("indices initialized.");
    }

    @Override
    public ScanResponse scanAndStoreSubDirAsync(ScanRequest query) throws InterruptedException {

        if (!Files.isDirectory(query.getPath())) {
            LOG.error("Expected directory path ! {}", query.getPath());
            return ScanResponse.getError(query.getPath());
        }

        LOG.info("Scanning subdirectory ...");
        String regexp = query.getPath().toString() + "/.*";

        LOG.info("Querying existing records for {}", regexp);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.regexpQuery("path", regexp));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(1_000);

        SearchObserver searchObserver = new SearchObserver();

        elasticSearchService.getDocuments(FileInfo.class, searchObserver, searchSourceBuilder);
        while(!searchObserver.await(1, TimeUnit.SECONDS));

        LOG.info("Deleting {} existing records ...", searchObserver.getDocumentIds().size());
        int deleteProgress = 0;
        for(FileInfoId fileInfoId: searchObserver.getDocumentIds()) {
            deleteProgress++;
            LOG.info("deleting {}/{} {}", deleteProgress, searchObserver.getDocumentIds().size(), fileInfoId.getId());
            deleteDocumentById(FileInfo.class, fileInfoId.getId());
            deleteDocumentById(ImageMetaDataInfo.class, fileInfoId.getId());
            deleteDocumentById(VideoMetaDataInfo.class, fileInfoId.getId());
            deleteDocumentById(AnnotationMetaData.class, fileInfoId.getId());
            deleteDocumentById(UnmappedData.class, fileInfoId.getId());
            deleteDocumentById(ObjectRecognition.class, fileInfoId.getId());
        }

        LOG.info("Commencing  directory scan ...");
        Observable<DirItem> dirItemObservable = dirScanner.scanDirectoryAsync(query);
        FsObserver fsObserver = new FsObserver(elasticSearchService, mediaService, query.getMetaDataFileName(), mlScannerService);
        dirItemObservable.subscribe(fsObserver);
        fsObserver.awaitSubscribed(10, TimeUnit.SECONDS);
        LOG.info("Subscription completed");
        fsObserver.awaitCompleted();
        LOG.info("Scan completed.");
        return ScanResponse.getSuccess(query.getPath(), fsObserver.getRecords(), searchObserver.getDocumentIds().size(),
                fsObserver.getDirCounter(), fsObserver.getErrors(), fsObserver.getAnnotations());
    }

    @Override
    public void cleanUnmappedData() {
        LOG.info("deleting unmapped-data ...");
        deleteIndex(UnmappedData.class);
        createIndex(UnmappedData.class);
    }

    @Override
    public void closeAndWaitForExecutors() throws Exception {
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS));
        mlExecutorService.shutdown();
        while(!mlExecutorService.awaitTermination(1, TimeUnit.SECONDS));
        LOG.info("waiting for es executors");
        elasticSearchService.closeAndWaitForExecutors();
    }

    @Override
    public long getMlTaskCount() {
        return mlScannerService.getTaskCount();
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
        mlExecutorService.shutdown();
        elasticSearchService.close();
    }

    private void deleteIndex(Class<?> type) {
        try {
            this.elasticSearchService.deleteIndex(type);
            LOG.info("index deleted");
        } catch (IOException e) {
            LOG.error("ERROR deleting index: {}", e.getMessage());
        }
    }

    private void createIndex(Class<?> type) {
        try {
            this.elasticSearchService.createIndex(type);
            LOG.info("index created");
        } catch (IOException e) {
            LOG.error("ERROR created index: {}", e.getMessage());
        }
    }

    private void deleteDocumentById(Class<?> type, String documentId) {
        try {
            elasticSearchService.deleteDocumentById(type, new DocumentId(documentId));
        } catch (IOException e) {
            LOG.error("Delete action has failed !", e);
        }
    }

}
