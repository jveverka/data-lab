package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;

import java.util.Collection;
import java.util.Optional;

public interface ElasticSearchService {

    <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer);

    <T> boolean createIndex(Class<T> type);

    <T> boolean deleteIndex(Class<T> type);

    <T> boolean hasIndex(Class<T> type);

    <T> boolean saveDocument(Class<T> type, Object data);

    <T> Optional<T> getDocumentById(Class<T> type, DocumentId id);

    <T> Collection<T> getDocuments(Class<T> type);

    <T> boolean deleteDocumentById(Class<T> type, Object data);

}
