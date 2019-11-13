package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.Emitter;
import itx.fs.service.FSUtils;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;


public class FileSystemDirScanner implements DirScanner {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemDirScanner.class);

    @Override
    public void scanDirectory(Emitter<DirItem> emitter, DirQuery query) throws IOException {
        Files.walkFileTree(query.getPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                if (attributes.isRegularFile()) {
                    try {
                        CheckSum checkSum = FSUtils.calculateChecksum(path, FSUtils.SHA256);
                        emitter.onNext(new DirItem(path, attributes, checkSum));
                    } catch (NoSuchAlgorithmException e) {
                        emitter.onError(e);
                        LOG.error("Checksum exception: ", e);
                    }
                } else {
                    emitter.onNext(new DirItem(path, attributes));
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
