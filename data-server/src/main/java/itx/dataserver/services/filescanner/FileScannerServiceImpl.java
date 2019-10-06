package itx.dataserver.services.filescanner;

import io.reactivex.rxjava3.core.Flowable;
import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.dataserver.services.filescanner.dto.FileInfoDataTransformer;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.fs.service.FSService;
import itx.fs.service.FSServiceImpl;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.image.service.ImageService;
import itx.image.service.ImageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileScannerServiceImpl implements FileScannerService {

    private static final Logger LOG = LoggerFactory.getLogger(FileScannerServiceImpl.class);

    private final Path rootPath;
    private final FSService dirScanner;
    private final ElasticSearchService elasticSearchService;
    private final ImageService imageService;
    private final ExecutorService executorService;

    public FileScannerServiceImpl(Path rootPath, ClientConfig config) {
        LOG.info("FileScannerService: initializing ...");
        this.rootPath = rootPath;
        executorService = Executors.newFixedThreadPool(8);
        this.dirScanner = new FSServiceImpl(executorService);
        this.elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        this.elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
        this.imageService = new ImageServiceImpl();
        try {
            this.elasticSearchService.deleteIndex(FileInfo.class);
            LOG.info("index deleted");
        } catch (IOException e) {
            LOG.error("ERROR deleting index: {}", e.getMessage());
        }
        try {
            this.elasticSearchService.createIndex(FileInfo.class);
            LOG.info("index created");
        } catch (IOException e) {
            LOG.error("ERROR creating index: {}", e.getMessage());
        }
    }

    @Override
    public void scanAndStoreRoot() throws IOException, InterruptedException {
        LOG.info("scanning ...");
        DirQuery query = new DirQuery(rootPath);
        FsSubscriber subscriber = new FsSubscriber(elasticSearchService, imageService);
        Flowable<DirItem> dirItemFlowable = dirScanner.scanDirectory(query);
        dirItemFlowable.subscribe(subscriber);
        subscriber.awaitSubscribed(10, TimeUnit.SECONDS);
        LOG.info("subscription completed");
        while(!subscriber.isComplete()) {
            LOG.info("scanning 100");
            subscriber.requestData(100);
            subscriber.awaitRequested();
        }
        subscriber.getSubscription().cancel();
        LOG.info("scan completed.");
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
        elasticSearchService.close();
    }
}
