package itx.elastic.service.dto;

import java.util.Objects;

public class IndexName {

    private final String name;

    public IndexName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexName indexName = (IndexName) o;
        return Objects.equals(name, indexName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
