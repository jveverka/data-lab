package itx.dataserver;

import com.beust.jcommander.JCommander;
import itx.dataserver.services.filescanner.FileScannerService;
import itx.dataserver.services.filescanner.FileScannerServiceImpl;
import itx.dataserver.services.filescanner.dto.ScanRequest;
import itx.dataserver.services.filescanner.dto.ScanResponse;
import itx.elastic.service.dto.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.file.Files;
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
        if (!Files.isDirectory(rootPath)) {
            LOG.error("ERROR: directory path expected {}", arguments.getRootPath());
            return;
        }
        LOG.info("DsMain: exec={} rootPath={}", arguments.getExecutorSize(), rootPath);
        LOG.info("DsMain: ElasticSearch={}:{}", arguments.getElasticHost(), arguments.getElasticPort());
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint(arguments.getElasticHost(), arguments.getElasticPort(), "http")
                .build();
        LOG.info("DsMain: ML-Service={}:{}", arguments.getMlHost(), arguments.getMlPort());
        InetSocketAddress mlAddress = InetSocketAddress.createUnresolved(arguments.getMlHost(), arguments.getMlPort());
        try (FileScannerService scanner = new FileScannerServiceImpl(config, mlAddress, arguments.getExecutorSize())) {
            if (arguments.isInitIndices()) {
                LOG.info("DsMain: ES init indices ...");
                scanner.initIndices();
            } else {
                LOG.info("DsMain: ES indices initialization is skipped.");
            }
            LOG.info("DsMain: annotated-meta-data={}", arguments.getMetaDataFileName());
            ScanRequest scanRequest = new ScanRequest(rootPath, arguments.getExecutorSize(), arguments.getMetaDataFileName());
            ScanResponse scanResponse = scanner.scanAndStoreSubDirAsync(scanRequest);
            float durationSec = (System.nanoTime() - startTime) / 1_000_000_000F;
            LOG.info("DsMain: records deleted: {}", scanResponse.getDeletedRecords());
            LOG.info("DsMain: records created: {}", scanResponse.getCreatedRecords());
            LOG.info("DsMain: dirs scanned   : {}", scanResponse.getDirectories());
            LOG.info("DsMain: annotations    : {}", scanResponse.getAnnotations());
            LOG.info("DsMain: scan errors    : {}", scanResponse.getErrors());
            LOG.info("DsMain: success: {}", scanResponse.isSuccess());
            LOG.info("DsMain: done in {} s", durationSec);
            LOG.info("DsMain: speed = {} files/sec", scanResponse.getCreatedRecords() / durationSec);
            scanner.closeAndWaitForExecutors();
            LOG.info("DsMain: waiting for ML services to finish ...");
            durationSec = (System.nanoTime() - startTime) / 1_000_000_000F;
            LOG.info("DsMain: ML done in {} s", durationSec);
            LOG.info("DsMain: ML speed = {} files/sec", scanner.getMlTaskCount() / durationSec);
        }
    }

}
