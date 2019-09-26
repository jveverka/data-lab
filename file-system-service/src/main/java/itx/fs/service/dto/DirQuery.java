package itx.fs.service.dto;

import java.nio.file.Path;

public class DirQuery {

    private final Path path;

    public DirQuery(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

}
