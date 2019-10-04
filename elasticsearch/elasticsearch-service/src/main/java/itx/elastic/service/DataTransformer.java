package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

public interface DataTransformer <T> {

    XContentBuilder getSource(T data) throws IOException;

    XContentBuilder getIndexMapping() throws IOException;

    String getIndexName();

    DocumentId getDocumentId(T data);

    T getInstance(DocumentId id, Map<String, Object> source);

}
