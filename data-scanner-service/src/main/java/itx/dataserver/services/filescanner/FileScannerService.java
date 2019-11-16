package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.ScanRequest;
import itx.dataserver.services.filescanner.dto.ScanResponse;

/**
 * This service is responsible for scanning file system and records meta-data produced by file system scanning into
 * ElasticSearch database. This service DOES NOT change or modify data on disk/file system in any way.
 * Data on file system might be read-only.
 */
public interface FileScannerService extends AutoCloseable {

    /**
     * Init ElasticSearch indices.
     * This will delete all documents in indices, all data is lost.
     * All indices are deleted and than created again.
     */
    void initIndices();

    /**
     * re-scan directory. This process has two main phases:
     * 1. Delete all documents stored in ElasticSearch related to files in target directory recursively.
     * 2. Scan recursively complete directory structure inside target directory.
     * @param query containing root path to target directory.
     * @throws InterruptedException
     */
    ScanResponse scanAndStoreSubDirAsync(ScanRequest query) throws InterruptedException;

    /**
     * Delete all documents in unmapped-data index.
     */
    void cleanUnmappedData();

    /**
     * Close executors and wait until all jobs are finished.
     */
    void closeAndWaitForExecutors() throws Exception;

}
