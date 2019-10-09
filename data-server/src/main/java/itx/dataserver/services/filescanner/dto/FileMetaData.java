package itx.dataserver.services.filescanner.dto;

import itx.dataserver.services.filescanner.dto.metadata.MetaDataContainer;

import java.util.Collection;

public class FileMetaData {

    private final Collection<MetaDataContainer<?>> metaData;

    public FileMetaData(Collection<MetaDataContainer<?>> metaData) {
        this.metaData = metaData;
    }

    public Collection<MetaDataContainer<?>> getMetaData() {
        return metaData;
    }

}
