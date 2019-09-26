package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;

import java.util.concurrent.Executor;

public class DirItemSource implements FlowableOnSubscribe<DirItem> {

    private final Executor executor;
    private final DirQuery query;
    private final DirScanner dirScanner;

    public DirItemSource(Executor executor, DirQuery query, DirScanner dirScanner) {
        this.executor = executor;
        this.query = query;
        this.dirScanner = dirScanner;
    }

    @Override
    public void subscribe(FlowableEmitter<DirItem> emitter) throws Throwable {
        DirScannerTask dirScannerTask = new DirScannerTask(emitter, query, dirScanner);
        executor.execute(dirScannerTask);
    }

}
