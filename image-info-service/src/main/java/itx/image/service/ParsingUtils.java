package itx.image.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.image.service.model.DirectoryInfo;
import itx.image.service.model.ErrorInfo;
import itx.image.service.model.MetaData;
import itx.image.service.model.TagInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ParsingUtils {

    private ParsingUtils() {
    }

    public static MetaData createModel(InputStream is) throws ImageProcessingException, IOException {
        List<DirectoryInfo> directories = new ArrayList<>();
        Metadata metadata = ImageMetadataReader.readMetadata(is);
        for (Directory directory : metadata.getDirectories()) {
            List<TagInfo> tags = new ArrayList<>();
            for (Tag tag : directory.getTags()) {
                TagInfo tagInfo = new TagInfo(tag.getTagName(), tag.getDescription(), tag.getTagType());
                tags.add(tagInfo);
            }
            List<ErrorInfo> errors = new ArrayList<>();
            for (String error: directory.getErrors()) {
                errors.add(new ErrorInfo(error));
            }
            directories.add(new DirectoryInfo(directory.getName(), tags, errors));
        }
        return new MetaData(directories);
    }

    public static String printToJson(MetaData metaData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metaData);
    }

}
