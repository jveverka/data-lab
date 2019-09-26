package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.FlowableEmitter;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class FileSystemDirScanner implements DirScanner {

    @Override
    public void scanDirectory(FlowableEmitter<DirItem> emitter, DirQuery query) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(query.getPath());
        Files.walkFileTree(query.getPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes)
                    throws IOException {
                emitter.onNext(new DirItem(path, attributes));
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
