package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.SingleEmitter;
import itx.fs.service.fsaccess.FSUtils;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.DirItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

public class SingleScannerTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SingleScannerTask.class);

    private final SingleEmitter<DirItem> emitter;
    private final Path path;
    private final BasicFileAttributes attributes;

    public SingleScannerTask(SingleEmitter<DirItem> emitter, Path path, BasicFileAttributes attributes) {
        this.emitter = emitter;
        this.path = path;
        this.attributes = attributes;
    }

    @Override
    public void run() {
        if (attributes.isRegularFile()) {
            try {
                CheckSum checkSum = FSUtils.calculateSha256Checksum(path);
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
    }

}
