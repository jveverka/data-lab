package itx.elastic.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.DocumentId;

import java.io.IOException;
import java.util.Optional;

public interface ElasticSearchService {

    <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer);

    <T> void removeDataTransformer(Class<T> type);

    <T> boolean createIndex(Class<T> type) throws IOException;

    <T> boolean deleteIndex(Class<T> type) throws IOException;

    <T> boolean hasIndex(Class<T> type) throws IOException;

    <T> boolean saveDocument(Class<T> type, T data) throws IOException;

    <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) throws IOException;

    <T> void getDocuments(Class<T> type, Observer<T> observer) throws IOException;

    <T> boolean deleteDocumentById(Class<T> type, T data) throws IOException;

}
