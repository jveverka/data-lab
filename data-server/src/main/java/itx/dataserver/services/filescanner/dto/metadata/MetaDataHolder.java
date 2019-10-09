package itx.dataserver.services.filescanner.dto.metadata;

public interface MetaDataHolder<T> {

    String getGroupName();

    String getName();

    String getDescription();

    T getValue();

    MetaDataUnit getUnit();

}
