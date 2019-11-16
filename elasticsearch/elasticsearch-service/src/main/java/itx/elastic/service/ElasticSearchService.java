package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * This service provides access to ElasticSearch indices and maps document in indices to java POJOs.
 */
public interface ElasticSearchService extends AutoCloseable {

    /**
     * Register {@link DataTransformer} for particular class type.
     * Transformer is used when data is stored into ElasticSearch or fetched from ElasticSearch.
     * @param type java POJO class type.
     * @param transformer instance of transformer used for this class type.
     * @param <T>
     */
    <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer);

    /**
     * Remove transformer for particular class type.
     * @param type java POJO class type.
     * @param <T>
     * @return true if transformer has been removed.
     */
    <T> boolean removeDataTransformer(Class<T> type);

    /**
     * Create index in ElasticSearch.
     * @param type java POJO class for which is index created.
     * @param <T>
     * @return true if index has been created.
     * @throws IOException
     */
    <T> boolean createIndex(Class<T> type) throws IOException;

    /**
     * Delete index in ElasticSearch. All data in the index is lost !
     * @param type java POJO class for which the index is deleted.
     * @param <T>
     * @return true if index has been deleted.
     * @throws IOException
     */
    <T> boolean deleteIndex(Class<T> type) throws IOException;

    /**
     * Check if index for particular POJO exists in ElasticSearch.
     * @param type java POJO class.
     * @param <T>
     * @return true if index exists.
     * @throws IOException
     */
    <T> boolean hasIndex(Class<T> type) throws IOException;

    /**
     * Flush index. TODO
     * @param type
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> boolean flushIndex(Class<T> type) throws IOException;

    /**
     * Insert java POJO into ElasticSearch. This will write POJO data as document into ElasticSearch.
     * @param type java POJO class.
     * @param data data POJO instance.
     * @param <T>
     * @return true if document has been saved.
     * @throws IOException
     */
    <T> boolean saveDocument(Class<T> type, T data) throws IOException;

    /**
     * Get document by it's unique ID from ElasticSearch and transform it into java POJO.
     * @param type java POJO class.
     * @param id document unique ID. The ID is unique within particular index.
     * @param <T>
     * @return POJO data or empty if document does not exist.
     * @throws IOException
     * @throws DataMappingException
     */
    <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) throws IOException, DataMappingException;

    /**
     * Get all documents in the index. {@link Observer} receives asynchronously documents. This method is suitable to
     * retrieve large amounts of data from index.
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search-scroll.html
     * @param type the type of document.
     * @param observer receives asynchronously documents.
     * @param searchSize size of one search page. It is reasonable to keep search page size between 100 and 1000 documents.
     * @param <T>
     * @throws IOException
     */
    <T> void getDocuments(Class<T> type, Observer<T> observer, int searchSize);

    /**
     * Get all documents in the index matching search query.
     * @param type the type of document.
     * @param observer receives asynchronously documents matching search query.
     * @param searchSourceBuilder search query.
     * @param <T>
     */
    <T> void getDocuments(Class<T> type, Observer<T> observer, SearchSourceBuilder searchSourceBuilder);

    /**
     * Delete document from ElasticSearch index by it's unique ID.
     * @param type java POJO class.
     * @param id document unique ID. The ID is unique within particular index.
     * @param <T>
     * @return true if the document has been deleted.
     * @throws IOException
     */
    <T> boolean deleteDocumentById(Class<T> type, DocumentId id) throws IOException;

    /**
     * Close internal {@link ExecutorService} and wait until all it's jobs are finished.
     */
    void closeAndWaitForExecutors() throws Exception;

}
