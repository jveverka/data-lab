package itx.image.service;

import itx.image.service.model.MetaData;

import java.io.InputStream;
import java.util.Optional;

public interface ImageService {

    Optional<MetaData> getMetaData(InputStream stream);

}
