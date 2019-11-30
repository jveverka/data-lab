package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import itx.fs.service.dto.DirItem;
import itx.fs.service.fsaccess.FileDataReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;

public class SingleOnSubscribeScanner implements SingleOnSubscribe<DirItem> {

    private final Executor executor;
    private final Path path;
    private final FileDataReader fileDataReader;

    public SingleOnSubscribeScanner(Executor executor, Path path, FileDataReader fileDataReader) {
        this.executor = executor;
        this.path = path;
        this.fileDataReader = fileDataReader;
    }

    @Override
    public void subscribe(SingleEmitter<DirItem> emitter) throws Throwable {
        BasicFileAttributes attributes = fileDataReader.getBasicFileAttributes(path);
        executor.execute(new SingleScannerTask(emitter, path, attributes, fileDataReader));
    }

}
