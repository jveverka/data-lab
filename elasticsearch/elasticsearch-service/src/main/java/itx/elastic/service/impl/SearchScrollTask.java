package itx.elastic.service.impl;

import io.reactivex.rxjava3.core.Observer;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchScrollTask<T> implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SearchScrollTask.class);

    private final Observer<T> observer;
    private final RestHighLevelClient client;
    private final DataTransformer<T> dataTransformer;
    private final int searchSize;
    private final SearchSourceBuilder searchSourceBuilder;

    public SearchScrollTask(Observer<T> observer, RestHighLevelClient client, DataTransformer<T> dataTransformer,
                            int searchSize, SearchSourceBuilder searchSourceBuilder) {
        this.observer = observer;
        this.client = client;
        this.dataTransformer = dataTransformer;
        this.searchSize = searchSize;
        this.searchSourceBuilder = searchSourceBuilder;
    }

    @Override
    public void run() {
        LOG.info("SearchScrollTask: started");
        try {
            observer.onSubscribe(new DisposableNoop());

            SearchRequest searchRequest = new SearchRequest(dataTransformer.getIndexName());
            searchRequest.scroll(TimeValue.timeValueMinutes(5L));
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            final long foundHits = searchResponse.getHits().getHits().length;
            final long totalHits = searchResponse.getHits().getTotalHits().value;
            LOG.info("SearchScrollTask: search found {}/{}", foundHits, totalHits);

            for (SearchHit hit : searchResponse.getHits()) {
                observer.onNext(dataTransformer.getInstance(new DocumentId(hit.getId()), hit.getSourceAsMap()));
            }

            boolean scroll = searchResponse.getHits().getHits().length == searchSize;
            String scrollId = searchResponse.getScrollId();

            while (scroll) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueMinutes(5L));
                SearchResponse searchScrollResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);

                LOG.info("SearchScrollTask: scroll found {}", searchScrollResponse.getHits().getHits().length);

                scrollId = searchScrollResponse.getScrollId();
                for (SearchHit hit : searchScrollResponse.getHits()) {
                    observer.onNext(dataTransformer.getInstance(new DocumentId(hit.getId()), hit.getSourceAsMap()));
                }
                if (searchScrollResponse.getHits().getHits().length == 0) {
                    scroll = false;
                }
            }
            observer.onComplete();
            LOG.info("SearchScrollTask: done.");
        } catch (Exception e) {
            LOG.info("SearchScrollTask: {}}", e.getMessage());
            observer.onError(e);
        }
    }

}
