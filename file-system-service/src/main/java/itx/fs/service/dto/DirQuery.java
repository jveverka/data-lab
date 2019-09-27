package itx.fs.service.dto;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirQuery {

    private final Path path;

    public DirQuery(Path path) {
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
