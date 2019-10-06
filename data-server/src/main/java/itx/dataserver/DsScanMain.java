package itx.dataserver;

import com.beust.jcommander.JCommander;
import itx.dataserver.services.filescanner.DsScanArguments;
import itx.dataserver.services.filescanner.FileScannerService;
import itx.dataserver.services.filescanner.FileScannerServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DsScanMain {

    private static final Logger LOG = LoggerFactory.getLogger(DsScanMain.class);

    public static void main(String[] args) throws Exception {
        DsScanArguments arguments = new DsScanArguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        LOG.info("DsMain: started");
        long startTime = System.nanoTime();
        Path rootPath = Paths.get(arguments.getRootPath());
        LOG.info("DsMain: exec={} rootPath={}", arguments.getExecutorSize(), rootPath.toString());
        LOG.info("DsMain: ElasticSearch={}:{}", arguments.getElasticHost(), arguments.getElasticPort());
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint(arguments.getElasticHost(), arguments.getElasticPort(), "http")
                .build();
        FileScannerService scanner = new FileScannerServiceImpl(rootPath, config, arguments.getExecutorSize());
        scanner.scanAndStoreRootAsync();
        float durationSec = (System.nanoTime() - startTime)/1_000_000_000F;
        LOG.info("DsMain: done in {} s", durationSec);
        scanner.close();
    }

}
