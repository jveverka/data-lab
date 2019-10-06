package itx.dataserver;

import itx.dataserver.services.filescanner.FileScannerService;
import itx.dataserver.services.filescanner.FileScannerServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DsMain {

    private static final Logger LOG = LoggerFactory.getLogger(DsMain.class);

    public static void main(String[] args) throws Exception {
        LOG.info("DsMain: started");
        long startTime = System.nanoTime();
        Path rootPath = Paths.get(args[0]);
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        LOG.info("DsMain: rootPath={}", rootPath.toString());
        FileScannerService scanner = new FileScannerServiceImpl(rootPath, config);
        scanner.scanAndStoreRoot();
        float durationSec = (System.nanoTime() - startTime)/1_000_000_000F;
        LOG.info("DsMain: done in {} s", durationSec);
        scanner.close();
    }

}
