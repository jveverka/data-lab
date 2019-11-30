package itx.fs.service.test.mocks;

import itx.fs.service.fsaccess.FileDataReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FileDataReaderMock implements FileDataReader {

    private final Map<Path, FsItemMock> fsMock;

    public FileDataReaderMock(Path basePath, FsItemMock fsItemMock) {
        this.fsMock = new ConcurrentHashMap<>();
        FsItemMock root = new FsItemMock(basePath.toString(), fsItemMock.getDirs(), fsItemMock.getFiles());
        this.fsMock.put(basePath, root);
        scanFsItem(basePath, this.fsMock, fsItemMock);
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes(Path path) throws IOException {
        FsItemMock fsItemMock = fsMock.get(path);
        if (fsItemMock != null) {
            return new BasicFileAttributes() {

                @Override
                public FileTime lastModifiedTime() {
                    return null;
                }

                @Override
                public FileTime lastAccessTime() {
                    return null;
                }

                @Override
                public FileTime creationTime() {
                    return null;
                }

                @Override
                public boolean isRegularFile() {
                    return fsItemMock.isFile();
                }

                @Override
                public boolean isDirectory() {
                    return fsItemMock.isDirectory();
                }

                @Override
                public boolean isSymbolicLink() {
                    return false;
                }

                @Override
                public boolean isOther() {
                    return false;
                }

                @Override
                public long size() {
                    return 0;
                }

                @Override
                public Object fileKey() {
                    return null;
                }
            };
        }
        throw new UnsupportedOperationException("path not found: " + path.toString());
    }

    @Override
    public Optional<String[]> getDirectoryList(Path path) {
        FsItemMock fsItemMock = fsMock.get(path);
        if (fsItemMock != null && fsItemMock.isDirectory()) {
            return Optional.of(getList(fsItemMock));
        }
        throw new UnsupportedOperationException("path not found: " + path.toString());
    }

    @Override
    public boolean isDirectory(Path path) {
        FsItemMock fsItemMock = fsMock.get(path);
        if (fsItemMock != null) {
            return fsItemMock.isDirectory();
        }
        throw new UnsupportedOperationException("path not found: " + path.toString());
    }

    private static String[] getList(FsItemMock fsItemMock) {
        List<String> names = new ArrayList<>();
        if (fsItemMock.getDirs() != null) {
            for (int i = 0; i < fsItemMock.getDirs().length; i++) {
                names.add(fsItemMock.getDirs()[i].getName());
            }
        }
        if (fsItemMock.getFiles() != null) {
            for (int i = 0; i < fsItemMock.getFiles().length; i++) {
                names.add(fsItemMock.getFiles()[i].getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    private static void scanFsItem(Path basePath, Map<Path, FsItemMock> fsMock, FsItemMock fsItemMock) {
        if (fsItemMock.getDirs() != null) {
            for (FsItemMock fsItemMockDir : fsItemMock.getDirs()) {
                fsMock.put(Paths.get(basePath.toString(), fsItemMockDir.getName()), fsItemMockDir);
                scanFsItem(Paths.get(basePath.toString(), fsItemMockDir.getName()), fsMock, fsItemMockDir);
            }
        }
        if (fsItemMock.getFiles() != null) {
            for (FsItemMock fsItemMockFile : fsItemMock.getFiles()) {
                fsMock.put(Paths.get(basePath.toString(), fsItemMockFile.getName()), fsItemMockFile);
            }
        }
    }

}
