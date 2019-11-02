package itx.dataserver.services.filescanner;

import java.nio.file.Path;

public interface FileScannerService extends AutoCloseable {

    /**
     * Scans root path and save results into ElasticSearch.
     * This method deletes old indices in elastic search and create new ones.
     * All documents in indices from previous scan is destroyed.
     * @throws InterruptedException
     */
    void scanAndStoreRootAsync() throws InterruptedException;

    /**
     * Scans subdirectory of root path.
     * @param relativePath path within root directory.
     * @throws InterruptedException
     */
    void scanAndStoreSubDirAsync(Path relativePath) throws InterruptedException;

    /**
     * Fet root path of managed directory.
     * @return
     */
    Path getRoot();

    /**
     * Close executors and wait until all jobs are finished.
     */
    void closeAndWaitForExecutors() throws Exception;

}
