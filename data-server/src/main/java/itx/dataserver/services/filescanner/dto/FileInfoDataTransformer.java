package itx.dataserver.services.filescanner.dto;

import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Map;


public class FileInfoDataTransformer implements DataTransformer<FileInfo> {

    @Override
    public Map<String, Object> getSource(FileInfo data) {
        return null;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("fileSystemInfo");
                {
                    builder.field("path", "text");
                    builder.field("creationTime", "date");
                    builder.field("lastModifiedTime", "date");
                    builder.field("lastAccessTime", "date");
                    builder.field("type", "keyword");
                    builder.startObject("checksum");
                    {
                        builder.field("checksum", "keyword");
                        builder.field("algorithm", "keyword");
                    }
                    builder.endObject();
                }
                builder.endObject();
                builder.startObject("mediaInfo");
                {
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
        return "file-info";
    }

    @Override
    public DocumentId getDocumentId(FileInfo data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    public FileInfo getInstance(DocumentId id, Map<String, Object> source) {
        return null;
    }

}
