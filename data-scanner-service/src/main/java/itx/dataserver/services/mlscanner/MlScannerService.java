package itx.dataserver.services.mlscanner;

import itx.fs.service.dto.DirItem;

public interface MlScannerService {

    void onFileEvent(DirItem dirItem);

    long getTaskCount();

}
