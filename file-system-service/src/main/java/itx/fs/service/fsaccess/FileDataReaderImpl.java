package itx.fs.service.fsaccess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class FileDataReaderImpl implements FileDataReader {

    @Override
    public BasicFileAttributes getBasicFileAttributes(Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    @Override
    public Optional<String[]> getDirectoryList(Path path) {
        File file = path.toFile();
        if (file.isDirectory()) {
            return Optional.of(file.list());
        }
        return Optional.empty();
    }

    @Override
    public boolean isDirectory(Path path) {
        return path.toFile().isDirectory();
    }

}
