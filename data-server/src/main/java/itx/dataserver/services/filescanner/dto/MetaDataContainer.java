package itx.dataserver.services.filescanner.dto;

import itx.dataserver.services.filescanner.dto.metadata.MetaDataHolder;

import java.util.Collection;

public class MetaDataContainer {

    private final Collection<MetaDataHolder<?>> metaData;

    public MetaDataContainer(Collection<MetaDataHolder<?>> metaData) {
        this.metaData = metaData;
    }

    public Collection<MetaDataHolder<?>> getMetaData() {
        return metaData;
    }

}
