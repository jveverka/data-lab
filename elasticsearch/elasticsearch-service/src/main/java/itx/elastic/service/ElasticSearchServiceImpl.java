package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.elastic.service.impl.SearchScrollTask;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.SyncedFlushRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.SyncedFlushResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;


public class ElasticSearchServiceImpl implements ElasticSearchService {

    private static final String ERRROR_MESSAGE = "Missing DataTransformer for ";

    private final RestHighLevelClient client;
    private final Map<Class<?>, DataTransformer<?>> transformers;
    private final ExecutorService executorService;

    public ElasticSearchServiceImpl(ClientConfig config, ExecutorService executorService) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(executorService);
        this.transformers = new ConcurrentHashMap<>();
        this.client = new RestHighLevelClient(RestClient.builder(config.getHttpHostArray()));
        this.executorService = executorService;
    }

    @Override
    public <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer) {
        transformers.put(type, transformer);
    }

    @Override
    public <T> boolean removeDataTransformer(Class<T> type) {
        DataTransformer<?> dataTransformer = transformers.remove(type);
        return dataTransformer != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean createIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            try {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(dataTransformer.getIndexName());
                createIndexRequest.mapping(dataTransformer.getIndexMapping());
                CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                return createIndexResponse.isAcknowledged();
            } catch (ElasticsearchStatusException e) {
                //existing index, already created
                return true;
            }
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean deleteIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            try {
                DeleteIndexRequest request = new DeleteIndexRequest(dataTransformer.getIndexName());
                AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
                return response.isAcknowledged();
            } catch (ElasticsearchStatusException e) {
                //not existing index, already deleted
                return true;
            }
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean hasIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            GetIndexRequest getIndexRequest = new GetIndexRequest(dataTransformer.getIndexName());
            return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean flushIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            SyncedFlushRequest flushRequest = new SyncedFlushRequest(dataTransformer.getIndexName());
            SyncedFlushResponse syncedFlushResponse = client.indices().flushSynced(flushRequest, RequestOptions.DEFAULT);
            RefreshRequest refreshRequest = new RefreshRequest(dataTransformer.getIndexName());
            RefreshResponse refreshResponse = client.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
            return RestStatus.OK.equals(refreshResponse.getStatus()) && syncedFlushResponse.failedShards() == 0;
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean saveDocument(Class<T> type, T data) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            IndexRequest indexRequest = new IndexRequest(dataTransformer.getIndexName());
            indexRequest.id(dataTransformer.getDocumentId(data).getId());
            indexRequest.source(dataTransformer.getSource(data));
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return RestStatus.CREATED.equals(indexResponse.status());
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) throws IOException, DataMappingException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            GetRequest getRequest = new GetRequest(dataTransformer.getIndexName(), id.getId());
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                return Optional.of(dataTransformer.getInstance(id, getResponse.getSource()));
            }
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void getDocuments(Class<T> type, Observer<T> observer, int searchSize) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQuery());
        searchSourceBuilder.size(searchSize);
        getDocuments(type, observer, searchSourceBuilder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean deleteDocumentById(Class<T> type, DocumentId id) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            DeleteRequest deleteRequest = new DeleteRequest(dataTransformer.getIndexName(), id.getId());
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
            return RestStatus.OK.equals(deleteResponse.status());
        } else {
            throw new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void getDocuments(Class<T> type, Observer<T> observer, SearchSourceBuilder searchSourceBuilder) {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            SearchScrollTask<T> searchScrollTask = new SearchScrollTask<>(observer, client, dataTransformer, searchSourceBuilder.size(), searchSourceBuilder);
            executorService.submit(searchScrollTask);
        } else {
            UnsupportedOperationException exception = new UnsupportedOperationException(ERRROR_MESSAGE + type.getCanonicalName());
            observer.onError(exception);
            observer.onComplete();
            throw exception;
        }
    }

    @Override
    public void closeAndWaitForExecutors() throws Exception {
        client.close();
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Override
    public void close() throws Exception {
        client.close();
        executorService.shutdown();
    }



}
