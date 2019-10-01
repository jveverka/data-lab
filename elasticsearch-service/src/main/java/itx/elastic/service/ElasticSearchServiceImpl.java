package itx.elastic.service;

import itx.elastic.service.dto.DocumentId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ElasticSearchServiceImpl implements ElasticSearchService {

    private final Map<Class<?>, DataTransformer<?>> transformers;

    public ElasticSearchServiceImpl() {
        this.transformers = new HashMap<>();
    }

    @Override
    public <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer) {
        transformers.put(type, transformer);
    }

    @Override
    public <T> boolean createIndex(Class<T> type) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return false;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean deleteIndex(Class<T> type) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return false;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean hasIndex(Class<T> type) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return false;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean saveDocument(Class<T> type, Object data) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return false;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> Collection<T> getDocuments(Class<T> type) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return null;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean deleteDocumentById(Class<T> type, Object data) {
        DataTransformer<?> dataTransformer = transformers.get(type);
        if (dataTransformer != null) {

            return false;
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

}
