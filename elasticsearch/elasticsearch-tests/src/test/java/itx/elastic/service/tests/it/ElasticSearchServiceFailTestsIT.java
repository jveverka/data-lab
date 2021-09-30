package itx.elastic.service.tests.it;

import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.EventDataId;
import itx.elastic.service.tests.it.dto.EventDataTransformer;
import itx.elastic.service.tests.it.dto.Location;
import org.elasticsearch.ElasticsearchException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ElasticSearchServiceFailTestsIT {

    private ElasticSearchService elasticSearchService;
    private EventData eventData;

    @BeforeClass
    public void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 3200, "http")
                .build();
        elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        elasticSearchService.registerDataTransformer(EventData.class, new EventDataTransformer());
        eventData = EventData.from(new EventDataId("id1"), "name1", "description1", ZonedDateTime.now(), 3600, new Location(45.2F, 15.3F));
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testDisconnectedIndexExists() throws IOException {
        elasticSearchService.hasIndex(EventData.class);
        Assert.fail();
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testDisconnectedIndexCreate() throws IOException {
        elasticSearchService.createIndex(EventData.class);
        Assert.fail();
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testDisconnectedIndexDelete() throws IOException {
        elasticSearchService.deleteIndex(EventData.class);
        Assert.fail();
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testFlushIndex() throws IOException {
        elasticSearchService.flushIndex(EventData.class);
        Assert.fail();
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testDisconnectedDeleteDocument() throws IOException {
        elasticSearchService.deleteDocumentById(EventData.class, TestUtils.createDocumentId(eventData.getId()));
        Assert.fail();
    }

    @Test(expectedExceptions = { ElasticsearchException.class, IOException.class, ConnectException.class})
    public void testDisconnectedCreateDocument() throws IOException {
        elasticSearchService.saveDocument(EventData.class, eventData);
        Assert.fail();
    }

    @Test
    public void testDisconnectedGetDocuments() throws InterruptedException {
        TestObserver observer = new TestObserver();
        elasticSearchService.getDocuments(EventData.class, observer, 100);
        boolean await = observer.await(10, TimeUnit.SECONDS);
        Assert.assertTrue(await);
        Assert.assertTrue(observer.isFinished());
        Assert.assertNotNull(observer.getDisposable());
        Assert.assertTrue(observer.getErrors().size() == 1);
        Assert.assertTrue(observer.getDocs().size() == 0);
    }

    @AfterClass
    public void shutdown() throws Exception {
        elasticSearchService.close();
    }

}
