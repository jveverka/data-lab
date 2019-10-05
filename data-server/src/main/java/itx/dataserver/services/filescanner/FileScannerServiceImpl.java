package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.dataserver.services.filescanner.dto.FileInfoDataTransformer;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.scanner.DirScanner;
import itx.fs.service.scanner.FileSystemDirScanner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileScannerServiceImpl implements FileScannerService, AutoCloseable {

    private final Path rootPath;
    private final DirScanner dirScanner;
    private final ElasticSearchService elasticSearchService;

    public FileScannerServiceImpl(Path rootPath) {
        this.rootPath = rootPath;
        this.dirScanner = new FileSystemDirScanner();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        this.elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        this.elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
    }

    public void scanAndStore() throws IOException {
        DirQuery query = new DirQuery(rootPath);
        Emitter emitter = new Emitter(elasticSearchService);
        dirScanner.scanDirectory(emitter, query);
    }

    @Override
    public void close() throws Exception {
        elasticSearchService.close();
    }
}
