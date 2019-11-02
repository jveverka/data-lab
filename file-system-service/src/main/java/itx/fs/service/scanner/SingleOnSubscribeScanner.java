package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import itx.fs.service.dto.DirItem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;

public class SingleOnSubscribeScanner implements SingleOnSubscribe<DirItem> {

    private final Executor executor;
    private final Path path;

    public SingleOnSubscribeScanner(Executor executor, Path path) {
        this.executor = executor;
        this.path = path;
    }

    @Override
    public void subscribe(SingleEmitter<DirItem> emitter) throws Throwable {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        executor.execute(new SingleScannerTask(emitter, path, attributes));
    }

}
