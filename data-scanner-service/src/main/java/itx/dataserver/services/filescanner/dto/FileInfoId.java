package itx.dataserver.services.filescanner.dto;

import java.util.Objects;

public class FileInfoId {

    private final String id;

    public FileInfoId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfoId that = (FileInfoId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
