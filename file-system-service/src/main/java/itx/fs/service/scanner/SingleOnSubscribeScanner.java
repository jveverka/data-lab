package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.DirItem;
import itx.fs.service.fsaccess.FileDataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

public class SingleOnSubscribeScanner implements SingleOnSubscribe<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(SingleOnSubscribeScanner.class);

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
        executor.execute(() -> {
            if (attributes.isRegularFile()) {
                try {
                    CheckSum checkSum = fileDataReader.calculateSha256Checksum(path);
                    emitter.onSuccess(new DirItem(path, attributes, checkSum));
                } catch (NoSuchAlgorithmException e) {
                    emitter.onError(e);
                    LOG.error("Checksum exception: ", e);
                } catch (IOException e) {
                    emitter.onError(e);
                    LOG.error("IOException exception: ", e);
                }
            } else {
                emitter.onSuccess(new DirItem(path, attributes));
            }
        });
    }

}
