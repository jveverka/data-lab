package itx.elastic.service.tests.it;

import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.EventDataTransformer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticSearchServiceIndexTestsIT {

    private ElasticSearchService elasticSearchService;

    @BeforeClass
    public void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
    }

    @BeforeMethod
    public void beforeMethod() throws IOException {
        elasticSearchService.registerDataTransformer(EventData.class, new EventDataTransformer());
        elasticSearchService.deleteIndex(EventData.class);
    }

    @Test
    public void testElasticSearchServiceCreateAndDeleteIndex() throws IOException {
        boolean result = false;
        result = elasticSearchService.hasIndex(EventData.class);
        Assert.assertFalse(result);
        result = elasticSearchService.createIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.hasIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.deleteIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.hasIndex(EventData.class);
        Assert.assertFalse(result);
    }

    @Test
    public void testElasticSearchServiceCreateIndexTwice() throws IOException {
        boolean result = false;
        result = elasticSearchService.createIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.createIndex(EventData.class);
        Assert.assertTrue(result);
    }

    @Test
    public void testElasticSearchServiceDeleteIndexTwice() throws IOException {
        boolean result = false;
        result = elasticSearchService.createIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.deleteIndex(EventData.class);
        Assert.assertTrue(result);
        result = elasticSearchService.deleteIndex(EventData.class);
        Assert.assertTrue(result);
    }

    @AfterMethod
    public void afterMethod() throws IOException {
        elasticSearchService.deleteIndex(EventData.class);
        elasticSearchService.removeDataTransformer(EventData.class);
    }

    @AfterClass
    public void shutdown() throws Exception {
        elasticSearchService.close();
    }

}
