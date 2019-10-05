package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.DocumentId;

import java.io.IOException;
import java.util.Optional;

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
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search-scroll.html
     * @param type
     * @param observer
     * @param searchSize
     * @param <T>
     * @throws IOException
     */
    <T> void getDocuments(Class<T> type, Observer<T> observer, int searchSize);

    <T> boolean deleteDocumentById(Class<T> type, DocumentId id) throws IOException;

}
