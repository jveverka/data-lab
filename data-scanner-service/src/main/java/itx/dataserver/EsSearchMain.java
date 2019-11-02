package itx.dataserver;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfo;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoDataTransformer;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfoTransformer;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedDataTransformer;
import itx.dataserver.services.query.TestObserver;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

public class EsSearchMain {

    private static final Logger LOG = LoggerFactory.getLogger(EsSearchMain.class);

    public static void main(String[] args) throws Exception {
        LOG.info("EsSearch: started");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        ElasticSearchService elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        FileInfoDataTransformer fileInfoDataTransformer = new FileInfoDataTransformer();
        MetaDataInfoTransformer metaDataInfoTransformer = new MetaDataInfoTransformer();
        UnmappedDataTransformer unmappedDataTransformer = new UnmappedDataTransformer();
        elasticSearchService.registerDataTransformer(FileInfo.class, fileInfoDataTransformer);
        elasticSearchService.registerDataTransformer(MetaDataInfo.class, metaDataInfoTransformer);
        elasticSearchService.registerDataTransformer(UnmappedData.class, unmappedDataTransformer);

        //MatchAllQueryBuilder matchAllQueryBuilder = matchAllQuery();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.regexpQuery("path", "/datapool/juraj/Photos/2015_Photo-Album/.*"));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(1_000);

        TestObserver testObserver = new TestObserver();
        elasticSearchService.getDocuments(FileInfo.class, testObserver, searchSourceBuilder);

        testObserver.await(10, TimeUnit.HOURS);
        int documentCount = testObserver.getDocumentCount();

        LOG.info("EsSearch: docs {}", documentCount);

        elasticSearchService.closeAndWaitForExecutors();
        LOG.info("EsSearch: done.");
    }

}
