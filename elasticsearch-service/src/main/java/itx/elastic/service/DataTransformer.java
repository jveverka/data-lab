package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

public interface DataTransformer <T> {

    XContentBuilder getSource(T data);

    XContentBuilder getIndexMapping();

    String getIndexName();

    DocumentId getDocumentId(T data);

    T getInstance(DocumentId id, Map<String, Object> source);

}
