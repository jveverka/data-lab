package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.Emitter;
import itx.fs.service.fsaccess.FSUtils;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.DirItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

public class FileScannerTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FileScannerTask.class);

    private final Emitter<DirItem> emitter;
    private final Path path;
    private final BasicFileAttributes attributes;

    public FileScannerTask(Emitter<DirItem> emitter, Path path, BasicFileAttributes attributes) {
        this.emitter = emitter;
        this.path = path;
        this.attributes = attributes;
    }

    @Override
    public void run() {
        if (attributes.isRegularFile()) {
            try {
                CheckSum checkSum = FSUtils.calculateSha256Checksum(path);
                emitter.onNext(new DirItem(path, attributes, checkSum));
            } catch (NoSuchAlgorithmException e) {
                emitter.onError(e);
                LOG.error("Checksum exception: ", e);
            } catch (IOException e) {
                emitter.onError(e);
                LOG.error("IOException exception: ", e);
            }
        } else {
            emitter.onNext(new DirItem(path, attributes));
        }
    }

}
