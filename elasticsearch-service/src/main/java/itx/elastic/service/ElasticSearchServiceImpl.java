package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ElasticSearchServiceImpl implements ElasticSearchService {

    private final RestHighLevelClient client;
    private final Map<Class<?>, DataTransformer<?>> transformers;

    public ElasticSearchServiceImpl(ClientConfig config) {
        this.transformers = new ConcurrentHashMap<>();
        HttpHost[] hosts = config.getEndpoints().toArray(new HttpHost[config.getEndpoints().size()]);
        this.client = new RestHighLevelClient(RestClient.builder(hosts));
    }

    @Override
    public <T> void registerDataTransformer(Class<T> type, DataTransformer<T> transformer) {
        transformers.put(type, transformer);
    }

    @Override
    public <T> void removeDataTransformer(Class<T> type) {
        transformers.remove(type);
    }

    @Override
    public <T> boolean createIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(dataTransformer.getIndexName());
            createIndexRequest.mapping(dataTransformer.getIndexMapping());
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            return createIndexResponse.isAcknowledged();
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean deleteIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            GetIndexRequest request = new GetIndexRequest(dataTransformer.getIndexName());
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean hasIndex(Class<T> type) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(dataTransformer.getIndexName());
            AcknowledgedResponse deleteResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            return deleteResponse.isAcknowledged();
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean saveDocument(Class<T> type, T data) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            IndexRequest indexRequest = new IndexRequest(dataTransformer.getIndexName());
            indexRequest.id(dataTransformer.getDocumentId(data).getId());
            indexRequest.source(dataTransformer.getSource(data));
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            return RestStatus.CREATED.equals(indexResponse.status());
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> Optional<T> getDocumentById(Class<T> type, DocumentId id) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            GetRequest getRequest = new GetRequest(dataTransformer.getIndexName(), id.getId());
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                return Optional.of(dataTransformer.getInstance(id, getResponse.getSource()));
            }
            return Optional.empty();
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> void getDocuments(Class<T> type, Observer<T> observer) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            SearchRequest searchRequest = new SearchRequest(dataTransformer.getIndexName());
            observer.onSubscribe(new DisposableNoop());
            ActionListener<SearchResponse> listener = new ActionListener<> () {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    SearchHit[] hits = searchResponse.getHits().getHits();
                    for (SearchHit hit: hits) {
                        observer.onNext(dataTransformer.getInstance(new DocumentId(hit.getId()), hit.getSourceAsMap()));
                    }
                    observer.onComplete();
                }
                @Override
                public void onFailure(Exception e) {
                    observer.onError(e);
                }
            };
            client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);
            //USE SCROLL !!! client.scrollAsync();
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

    @Override
    public <T> boolean deleteDocumentById(Class<T> type, T data) throws IOException {
        DataTransformer<T> dataTransformer = (DataTransformer<T>)transformers.get(type);
        if (dataTransformer != null) {
            DeleteRequest deleteRequest = new DeleteRequest(dataTransformer.getIndexName(), dataTransformer.getDocumentId(data).getId());
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
            return RestStatus.OK.equals(deleteResponse.status());
        } else {
            throw new UnsupportedOperationException("Missing DataTransformer for " + type.getCanonicalName());
        }
    }

}
