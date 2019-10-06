package itx.dataserver;

import itx.dataserver.services.filescanner.FileScannerService;
import itx.dataserver.services.filescanner.FileScannerServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DsMain {

    private static final Logger LOG = LoggerFactory.getLogger(DsMain.class);

    public static void main(String[] args) throws IOException {
        LOG.info("DsMain: started");
        Path rootPath = Paths.get(args[0]);
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        LOG.info("DsMain: rootPath={}", rootPath.toString());
        FileScannerService scanner = new FileScannerServiceImpl(rootPath, config);
        scanner.scanAndStoreRoot();
        LOG.info("DsMain: done.");
    }

}
