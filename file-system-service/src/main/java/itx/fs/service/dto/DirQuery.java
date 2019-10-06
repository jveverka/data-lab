package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class DirQuery {

    private final Path path;
    private final int executorSize;

    public DirQuery(Path path) {
        Objects.requireNonNull(path);
        this.path = path;
        this.executorSize = 1;
    }

    public DirQuery(Path path, int executorSize) {
        Objects.requireNonNull(path);
        this.path = path;
        this.executorSize = executorSize >= 1 ? executorSize : 1;
    }

    public Path getPath() {
        return path;
    }

    public static DirQuery create(String path) {
        return new DirQuery(Paths.get(path));
    }

    public static DirQuery create(Path path) {
        return new DirQuery(path);
    }

    public int getExecutorSize() {
        return executorSize;
    }

}
