package itx.dataserver.services.filescanner.dto;

import itx.fs.service.dto.DirQuery;

import java.nio.file.Path;

public class ScanRequest extends DirQuery {

    private final String metaDataFileName;

    public ScanRequest(Path path, String metaDataFileName) {
        super(path);
        this.metaDataFileName = metaDataFileName;
    }

    public ScanRequest(Path path, int executorSize, String metaDataFileName) {
        super(path, executorSize);
        this.metaDataFileName = metaDataFileName;
    }

    public String getMetaDataFileName() {
        return metaDataFileName;
    }

}
