package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class DirQuery {

    private final Path path;

    public DirQuery(Path path) {
        Objects.requireNonNull(path);
        this.path = path;
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

}
