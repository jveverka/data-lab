package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

public interface DataTransformer<T> {

    Map<String, Object> getSource(T data);

    XContentBuilder getIndexMapping() throws IOException;

    String getIndexName();

    DocumentId getDocumentId(T data);

    T getInstance(DocumentId id, Map<String, Object> source);

}