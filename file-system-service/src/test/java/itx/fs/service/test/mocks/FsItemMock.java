package itx.fs.service.test.mocks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FsItemMock {

    private final String name;
    private final FsItemMock[] dirs;
    private final FsItemMock[] files;

    @JsonCreator
    public FsItemMock(@JsonProperty("name") String name,
                      @JsonProperty("dirs") FsItemMock[] dirs,
                      @JsonProperty("files")  FsItemMock[] files) {
        this.name = name;
        this.dirs = dirs;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public FsItemMock[] getDirs() {
        return dirs;
    }

    public FsItemMock[] getFiles() {
        return files;
    }

    public boolean isDirectory() {
        return (dirs != null) || (files != null);
    }

    public boolean isFile() {
        return (dirs == null) || (files == null);
    }

    public int getSize() {
        int size = 0;
        if (dirs != null) {
            size = size + dirs.length;
        }
        if (files != null) {
            size = size + files.length;
        }
        return size;
    }

}
