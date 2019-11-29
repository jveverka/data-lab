package itx.fs.service.test.mocks;

import itx.fs.service.fsaccess.FileDataReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileDataReaderMock implements FileDataReader {

    private final Path basePath;
    private final Map<Path, Boolean> files;

    public FileDataReaderMock(Path basePath) {
        this.basePath = basePath;
        this.files = new ConcurrentHashMap<>();
        this.files.put(basePath, true);
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes(Path path) throws IOException {
        return null;
    }

    @Override
    public Optional<String[]> getDirectoryList(Path path) {
        return Optional.empty();
    }

    @Override
    public boolean isDirectory(Path path) {
        return false;
    }

}
