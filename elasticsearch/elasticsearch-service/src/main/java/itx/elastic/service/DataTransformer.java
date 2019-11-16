package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * This transformer provides context when reading and writing data from / into ElasticSearch.
 * Each data type should have it's own transformer.
 * @param <T>
 */
public interface DataTransformer<T> {

    /**
     * Transform java POJO instance into source data.
     * @param data java POJO instance.
     * @return mapped data to be stored into ElasticSearch.
     */
    Map<String, Object> getSource(T data);

    /**
     * Get ElasticSearch mapping for this POJO type.
     * @return
     * @throws IOException
     */
    XContentBuilder getIndexMapping() throws IOException;

    /**
     * Get index name for this POJO type.
     * @return index name.
     */
    String getIndexName();

    /**
     * Get unique document ID for this instance.
     * @param data java POJO instance.
     * @return {@link DocumentId} to be used by ElasticSearch. This ID must be unique for given index.
     */
    DocumentId getDocumentId(T data);

    /**
     * Transform data
     * @param id {@link DocumentId} from ElasticSearch.
     * @param source raw document data from ElasticSearch.
     * @return java POJO instance.
     * @throws DataMappingException
     */
    T getInstance(DocumentId id, Map<String, Object> source) throws DataMappingException;

}
