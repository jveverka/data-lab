package itx.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import itx.image.service.dto.ExifInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageServiceImpl implements ImageService {

    @Override
    public Optional<ExifInfo> getExifInfo(InputStream stream) {
        try {
            ExifInfo.Builder builder = new ExifInfo.Builder();
            Metadata metadata = ImageMetadataReader.readMetadata(stream);
            for (Directory directory: metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (ParsingUtils.IMAGE_HEIGHT.equals(tag.getTagName())) {
                        builder.setHeight(ParsingUtils.getIntegerFromData(tag.getDescription()));
                    } else if (ParsingUtils.IMAGE_WIDTH.equals(tag.getTagName())) {
                        builder.setWidth(ParsingUtils.getIntegerFromData(tag.getDescription()));
                    } else if (ParsingUtils.COMPRESSION_TYPE.equals(tag.getTagName())) {
                        builder.setCompressionType(tag.getDescription());
                    } else if (ParsingUtils.DATA_PRECISION.equals(tag.getTagName())) {
                        builder.setPrecisionBites(ParsingUtils.getIntegerFromData(tag.getDescription()));
                    }
                }
            }
            return Optional.of(builder.build());
        } catch (ImageProcessingException | IOException e) {
            return Optional.empty();
        }
    }

}
