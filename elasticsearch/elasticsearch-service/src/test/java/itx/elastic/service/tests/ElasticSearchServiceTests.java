package itx.elastic.service.tests;

import itx.elastic.service.DataTransformer;
import itx.elastic.service.ESUtils;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import org.apache.http.HttpHost;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;

public class ElasticSearchServiceTests {

    @Test
    public void testElasticSearchServiceInitAndShutdown() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        DataTransformer<Object> dataTransformer = mock(DataTransformer.class);
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        ElasticSearchService elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        elasticSearchService.registerDataTransformer(Object.class, dataTransformer);
        boolean result = elasticSearchService.removeDataTransformer(Object.class);
        Assert.assertTrue(result);
        result = elasticSearchService.removeDataTransformer(Object.class);
        Assert.assertFalse(result);
        elasticSearchService.close();
    }

    @Test
    public void testClientConfig() {
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("host1", 8080, "http")
                .addEndPoint("host2", 8443, "https")
                .build();
        Assert.assertNotNull(config);
        HttpHost[] httpHostArray = config.getHttpHostArray();
        Assert.assertNotNull(httpHostArray);
        Assert.assertTrue(httpHostArray.length == 2);
    }

    @Test
    public void testZonedDateTimeConversions() {
        ZonedDateTime zonedDateTimeOriginal = ESUtils.fromString("20191004T163747.340+0200");
        String zoneDateTimeStringFromOriginal = ESUtils.toString(zonedDateTimeOriginal);
        ZonedDateTime zonedDateTimeParsed = ESUtils.fromString(zoneDateTimeStringFromOriginal);
        String zoneDateTimeStringFromParsed = ESUtils.toString(zonedDateTimeParsed);
        Assert.assertEquals(zoneDateTimeStringFromOriginal, zoneDateTimeStringFromParsed);
        Assert.assertEquals(zonedDateTimeOriginal, zonedDateTimeParsed);
    }

}
