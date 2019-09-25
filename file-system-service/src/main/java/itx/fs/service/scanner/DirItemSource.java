package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import itx.fs.service.dto.DirItem;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class DirItemSource implements FlowableOnSubscribe<DirItem> {

    private final Executor executor;
    private final Path rootPath;

    public DirItemSource(Executor executor, Path rootPath) {
        this.executor = executor;
        this.rootPath = rootPath;
    }

    @Override
    public void subscribe(FlowableEmitter<DirItem> emitter) throws Throwable {
        DirScannerTask dirScannerTask = new DirScannerTask(emitter, rootPath);
        executor.execute(dirScannerTask);
    }

}
