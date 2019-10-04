package itx.elastic.service;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class SearchScrollTask<T> implements Runnable {

    private final Observer<T> observer;
    private final RestHighLevelClient client;
    private final DataTransformer<T> dataTransformer;

    public SearchScrollTask(Observer<T> observer, RestHighLevelClient client, DataTransformer<T> dataTransformer) {
        this.observer = observer;
        this.client = client;
        this.dataTransformer = dataTransformer;
    }

    @Override
    public void run() {
        try {
            int searchSize = 100;
            observer.onSubscribe(new DisposableNoop());

            SearchRequest searchRequest = new SearchRequest(dataTransformer.getIndexName());
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchAllQuery());
            searchSourceBuilder.size(searchSize);
            searchRequest.scroll(TimeValue.timeValueMinutes(1L));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits()) {
                observer.onNext(dataTransformer.getInstance(new DocumentId(hit.getId()), hit.getSourceAsMap()));
            }

            boolean scroll = searchResponse.getHits().getHits().length == searchSize;
            String scrollId = searchResponse.getScrollId();

            while (scroll) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(30));
                SearchResponse searchScrollResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchScrollResponse.getScrollId();
                for (SearchHit hit : searchScrollResponse.getHits()) {
                    observer.onNext(dataTransformer.getInstance(new DocumentId(hit.getId()), hit.getSourceAsMap()));
                }
                if (searchScrollResponse.getHits().getHits().length == 0) {
                    scroll = false;
                }
            }

            observer.onComplete();
        } catch (IOException e) {
            observer.onError(e);
        }
    }

}
