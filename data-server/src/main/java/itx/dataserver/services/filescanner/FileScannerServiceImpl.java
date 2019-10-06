package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.dataserver.services.filescanner.dto.FileInfoDataTransformer;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.scanner.DirScanner;
import itx.fs.service.scanner.FileSystemDirScanner;
import itx.image.service.ImageService;
import itx.image.service.ImageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileScannerServiceImpl implements FileScannerService {

    private static final Logger LOG = LoggerFactory.getLogger(FileScannerServiceImpl.class);

    private final Path rootPath;
    private final DirScanner dirScanner;
    private final ElasticSearchService elasticSearchService;
    private final ImageService imageService;

    public FileScannerServiceImpl(Path rootPath, ClientConfig config) {
        LOG.info("FileScannerService: initializing ...");
        this.rootPath = rootPath;
        this.dirScanner = new FileSystemDirScanner();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        this.elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        this.elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
        this.imageService = new ImageServiceImpl();
        try {
            this.elasticSearchService.createIndex(FileInfo.class);
            LOG.info("index created");
        } catch (IOException e) {
            LOG.error("ERROR creating index: {}", e.getMessage());
        }
    }

    @Override
    public void scanAndStoreRoot() throws IOException {
        LOG.info("scanning ...");
        DirQuery query = new DirQuery(rootPath);
        Emitter emitter = new Emitter(elasticSearchService, imageService);
        dirScanner.scanDirectory(emitter, query);
    }

    @Override
    public void close() throws Exception {
        elasticSearchService.close();
    }
}
