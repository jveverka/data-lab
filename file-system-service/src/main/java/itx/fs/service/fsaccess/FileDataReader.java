package itx.fs.service.fsaccess;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public interface FileDataReader {

    BasicFileAttributes getBasicFileAttributes(Path path) throws IOException;

    Optional<String[]> getDirectoryList(Path path);

    boolean isDirectory(Path path);

}
