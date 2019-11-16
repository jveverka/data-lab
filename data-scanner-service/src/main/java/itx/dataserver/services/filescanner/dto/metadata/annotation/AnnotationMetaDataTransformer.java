package itx.dataserver.services.filescanner.dto.metadata.annotation;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.Coordinates;
import itx.elastic.service.DataMappingException;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnnotationMetaDataTransformer implements DataTransformer<AnnotationMetaData> {

    @Override
    public Map<String, Object> getSource(AnnotationMetaData data) {
        SimpleDateFormat sdf = new SimpleDateFormat(DataUtils.DATE_TIME_FORMAT);
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("path", data.getPath().toString());
        source.put("name", data.getName());
        source.put("description", data.getDescription());
        source.put("timeStamp", sdf.format(data.getTimeStamp()));
        Map<String, Object> coordinates = new HashMap<>();
        if (data.getCoordinates() != null) {
            coordinates.put("lon", data.getCoordinates().getLon());
            coordinates.put("lat", data.getCoordinates().getLat());
        }
        source.put("coordinates", coordinates);
        return source;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                DataUtils.addMappingField(builder, "fileInfoId", "keyword");
                DataUtils.addMappingField(builder, "path", "keyword");
                DataUtils.addMappingField(builder, "name", "text");
                DataUtils.addMappingField(builder, "description", "text");
                DataUtils.addDateMappingField(builder, "timeStamp", DataUtils.DATE_TIME_FORMAT);
                DataUtils.addMappingField(builder, "coordinates", "geo_point");
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public String getIndexName() {
        return "annotation-meta-data";
    }

    @Override
    public DocumentId getDocumentId(AnnotationMetaData data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public AnnotationMetaData getInstance(DocumentId id, Map<String, Object> source) throws DataMappingException {
        SimpleDateFormat sdf = new SimpleDateFormat(DataUtils.DATE_TIME_FORMAT);
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        Path path = Paths.get((String)source.get("path"));
        String name = (String)source.get("name");
        String description = (String)source.get("description");
        Date timeStamp = null;
        try {
            timeStamp = sdf.parse((String) source.get("timeStamp"));
        } catch (ParseException e) {
            throw new DataMappingException(e);
        }
        Map<String, Object> coordinatedSource = (Map<String, Object>) source.get("coordinates");
        Coordinates coordinates = new Coordinates((Float) coordinatedSource.get("lon"), (Float) coordinatedSource.get("lat"));
        return new AnnotationMetaData(fileInfoId, path, name, description, timeStamp, coordinates);
    }

}
