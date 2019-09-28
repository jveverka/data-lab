package itx.image.service;

import itx.image.service.dto.ExifInfo;

import java.io.InputStream;
import java.util.Optional;

public interface ImageService {

    Optional<ExifInfo> getExifInfo(InputStream stream);

}
