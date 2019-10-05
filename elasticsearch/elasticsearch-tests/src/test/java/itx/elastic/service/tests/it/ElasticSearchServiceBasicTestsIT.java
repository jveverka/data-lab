package itx.elastic.service.tests.it;

import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.EventDataTransformer;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticSearchServiceBasicTestsIT {

    private ElasticSearchService elasticSearchService;

    @BeforeTest
    public void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
    }

    @Test
    public void testElasticSearchServiceBasic() throws IOException {
        elasticSearchService.registerDataTransformer(EventData.class, new EventDataTransformer());
        elasticSearchService.hasIndex(EventData.class);
        boolean result = elasticSearchService.removeDataTransformer(EventData.class);
        Assert.assertTrue(result);
    }

    @Test
    public void testEventDataTransformer() {
        EventDataTransformer eventDataTransformer = new EventDataTransformer();
        EventData eventDataOriginal = TestUtils.createEventData(1);

        Assert.assertNotNull(eventDataOriginal);
        Map<String, Object> dataSource = eventDataTransformer.getSource(eventDataOriginal);
        EventData eventDataFromMap = eventDataTransformer.getInstance(new DocumentId(eventDataOriginal.getId()), dataSource);

        Assert.assertNotNull(eventDataFromMap);
        Assert.assertEquals(eventDataOriginal, eventDataFromMap);
    }

    @AfterTest
    public void shutdown() throws Exception {
        elasticSearchService.close();
    }

}
