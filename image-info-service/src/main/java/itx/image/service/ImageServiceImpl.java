package itx.image.service;


import com.drew.imaging.ImageProcessingException;
import itx.image.service.model.MetaData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageServiceImpl implements ImageService {

    @Override
    public Optional<MetaData> getMetaData(InputStream stream) {
        try {
            return Optional.of(ParsingUtils.createModel(stream));
        } catch (IOException | ImageProcessingException e) {
            return Optional.empty();
        }
    }

}
