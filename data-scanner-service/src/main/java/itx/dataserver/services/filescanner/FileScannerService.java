package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.ScanResponse;
import itx.fs.service.dto.DirQuery;

public interface FileScannerService extends AutoCloseable {

    /**
     * Init ElasticSearch indices.
     */
    void initIndices();

    /**
     * re-scan directory.
     * @param query containing root path to target directory.
     * @throws InterruptedException
     */
    ScanResponse scanAndStoreSubDirAsync(DirQuery query) throws InterruptedException;

    /**
     * Close executors and wait until all jobs are finished.
     */
    void closeAndWaitForExecutors() throws Exception;

}
