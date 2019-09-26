package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;

import java.io.IOException;

public interface DirScanner {

    void scanDirectory(FlowableEmitter<DirItem> emitter, DirQuery query) throws IOException;

}
