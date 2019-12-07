package itx.dataserver.services.mlscanner.dto;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.elastic.service.DataMappingException;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import itx.ml.service.odyolov3tf2.http.client.dto.DetectedObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectRecognitionDataTransformer implements DataTransformer<ObjectRecognition> {

    @Override
    public Map<String, Object> getSource(ObjectRecognition data) {
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("path", data.getPath().toString());

        List<Map<String, Object>> objectsList = new ArrayList<>();
        for (DetectedObject detectedObject: data.getObjects()) {
            Map<String, Object> object = new HashMap<>();
            object.put("classId", detectedObject.getClassId());
            object.put("score", detectedObject.getScore());
            objectsList.add(object);
        }
        source.put("objects", objectsList);

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
                DataUtils.addMappingField(builder, "objects.classId", "keyword");
                DataUtils.addMappingField(builder, "objects.score", "double");
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;    }

    @Override
    public String getIndexName() {
        return "object-recognition";
    }

    @Override
    public DocumentId getDocumentId(ObjectRecognition data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    public ObjectRecognition getInstance(DocumentId id, Map<String, Object> source) throws DataMappingException {
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        String path = (String)source.get("path");
        List<Map<String, Object>> objects = (List<Map<String, Object>>)source.get("objects");
        List<DetectedObject> detectedObjects = new ArrayList<>();

        for (Map<String, Object> object: objects) {
            String classId = (String)object.get("classId");
            Float score = (Float)object.get("score");
            DetectedObject detectedObject = new DetectedObject(classId, new Float[0], score);
            detectedObjects.add(detectedObject);
        }

        return new ObjectRecognition(fileInfoId, path, detectedObjects);
    }

}
