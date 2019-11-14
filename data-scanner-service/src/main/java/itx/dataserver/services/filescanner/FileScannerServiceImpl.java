package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.Observable;
import itx.dataserver.services.filescanner.dto.ScanResponse;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoDataTransformer;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfoTransformer;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfoTransformer;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedDataTransformer;
import itx.dataserver.services.query.SearchObserver;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.fs.service.FSService;
import itx.fs.service.FSServiceImpl;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.image.service.MediaService;
import itx.image.service.MediaServiceImpl;
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

    public FileScannerServiceImpl(ClientConfig config, int executorSize) {
        LOG.info("FileScannerService: initializing ...");
        this.executorService = Executors.newFixedThreadPool(executorSize);
        this.dirScanner = new FSServiceImpl(executorService);
        this.elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        ImageMetaDataInfoTransformer imageMetaDataInfoTransformer = new ImageMetaDataInfoTransformer();
        VideoMetaDataInfoTransformer videoMetaDataInfoTransformer = new VideoMetaDataInfoTransformer();
        UnmappedDataTransformer unmappedDataTransformer = new UnmappedDataTransformer();
        this.elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
        this.elasticSearchService.registerDataTransformer(ImageMetaDataInfo.class, imageMetaDataInfoTransformer);
        this.elasticSearchService.registerDataTransformer(VideoMetaDataInfo.class, videoMetaDataInfoTransformer);
        this.elasticSearchService.registerDataTransformer(UnmappedData.class, unmappedDataTransformer);
        this.mediaService = new MediaServiceImpl();
    }

    @Override
    public void initIndices() {
        LOG.info("deleting indices ...");
        deleteIndex(FileInfo.class);
        deleteIndex(ImageMetaDataInfo.class);
        deleteIndex(VideoMetaDataInfo.class);
        deleteIndex(UnmappedData.class);
        LOG.info("creating indices ...");
        createIndex(FileInfo.class);
        createIndex(ImageMetaDataInfo.class);
        createIndex(VideoMetaDataInfo.class);
        createIndex(UnmappedData.class);
        LOG.info("indices initialized.");
    }

    @Override
    public ScanResponse scanAndStoreSubDirAsync(DirQuery query) throws InterruptedException {

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
            try {
                elasticSearchService.deleteDocumentById(FileInfo.class, new DocumentId(fileInfoId.getId()));
            } catch (IOException e) {
                LOG.error("FileInfo delete action has failed !", e);
            }
            try {
                elasticSearchService.deleteDocumentById(ImageMetaDataInfo.class, new DocumentId(fileInfoId.getId()));
            } catch (IOException e) {
                LOG.error("MetaDataInfo delete action has failed !", e);
            }
            try {
                elasticSearchService.deleteDocumentById(UnmappedData.class, new DocumentId(fileInfoId.getId()));
            } catch (IOException e) {
                LOG.error("UnmappedData delete action has failed !", e);
            }
        }

        LOG.info("Commencing  directory scan ...");
        Observable<DirItem> dirItemObservable = dirScanner.scanDirectoryAsync(query);
        FsObserver fsObserver = new FsObserver(elasticSearchService, mediaService);
        dirItemObservable.subscribe(fsObserver);
        fsObserver.awaitSubscribed(10, TimeUnit.SECONDS);
        LOG.info("Subscription completed");
        fsObserver.awaitCompleted();
        LOG.info("Scan completed.");
        return ScanResponse.getSuccess(query.getPath(), fsObserver.getRecords(), searchObserver.getDocumentIds().size(), fsObserver.getDirCounter());
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
        LOG.info("waiting for es executor");
        elasticSearchService.closeAndWaitForExecutors();
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
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

}
