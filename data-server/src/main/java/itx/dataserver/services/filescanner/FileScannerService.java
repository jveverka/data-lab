package itx.dataserver.services.filescanner;

import java.io.IOException;

public interface FileScannerService extends AutoCloseable {

    void scanAndStoreRoot() throws IOException, InterruptedException;

}
