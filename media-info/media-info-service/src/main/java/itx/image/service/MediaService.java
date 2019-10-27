package itx.image.service;

import itx.image.service.model.MetaData;

import java.io.InputStream;
import java.util.Optional;

public interface MediaService {

    Optional<MetaData> getMetaData(InputStream stream);

}
