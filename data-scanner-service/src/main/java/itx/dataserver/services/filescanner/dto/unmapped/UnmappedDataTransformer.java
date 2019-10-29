package itx.dataserver.services.filescanner.dto.unmapped;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UnmappedDataTransformer implements DataTransformer<UnmappedData> {

    @Override
    public Map<String, Object> getSource(UnmappedData data) {
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("type", data.getType());
        source.put("jsonData", data.getJsonData());
        return source;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("fileInfoId"); {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("type"); {
                    builder.field("type", "keyword");
                }
                builder.endObject();

                builder.startObject("jsonData"); {
                    builder.field("type", "text");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public String getIndexName() {
        return "unmapped-data";
    }

    @Override
    public DocumentId getDocumentId(UnmappedData data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    public UnmappedData getInstance(DocumentId id, Map<String, Object> source) {
        FileInfoId fileInfoId = new FileInfoId(id.getId());
        String type = (String)source.get("type");
        String jsonData = (String)source.get("jsonData");
        return new UnmappedData(fileInfoId, type, jsonData);
    }

}
