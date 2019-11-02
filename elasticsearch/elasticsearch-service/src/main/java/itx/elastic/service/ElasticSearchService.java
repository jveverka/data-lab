package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public interface ElasticSearchService extends AutoCloseable {

    <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer);

    <T> boolean removeDataTransformer(Class<T> type);

    <T> boolean createIndex(Class<T> type) throws IOException;

    <T> boolean deleteIndex(Class<T> type) throws IOException;

    <T> boolean hasIndex(Class<T> type) throws IOException;

    <T> boolean flushIndex(Class<T> type) throws IOException;

    <T> boolean saveDocument(Class<T> type, T data) throws IOException;

    <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) throws IOException;

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

    <T> boolean deleteDocumentById(Class<T> type, DocumentId id) throws IOException;

    /**
     * Close internal {@link ExecutorService} and wait until all it's jobs are finished.
     */
    void closeAndWaitForExecutors() throws Exception;

}
