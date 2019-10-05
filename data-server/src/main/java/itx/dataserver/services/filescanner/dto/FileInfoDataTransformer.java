package itx.dataserver.services.filescanner.dto;

import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

public class FileInfoDataTransformer implements DataTransformer<FileInfo> {

    @Override
    public Map<String, Object> getSource(FileInfo data) {
        return null;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        return null;
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
