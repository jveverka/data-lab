package itx.elastic.service.tests.it;

import itx.elastic.service.DataMappingException;
import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.elastic.service.impl.ESUtils;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.EventDataId;
import itx.elastic.service.tests.it.dto.EventDataTransformer;
import itx.elastic.service.tests.it.dto.Location;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ElasticSearchServiceDocumentsTestsIT {

    private ElasticSearchService elasticSearchService;
    private EventData eventData;

    @BeforeClass
    public void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ClientConfig config = new ClientConfig.Builder()
                .addEndPoint("127.0.0.1", 9200, "http")
                .build();
        elasticSearchService = new ElasticSearchServiceImpl(config, executorService);
        eventData = EventData.from(new EventDataId("id1"), "name1", "description1", ESUtils.getNow(), 3600, new Location(45.2F, 15.3F));
    }

    @BeforeMethod
    public void beforeMethod() throws IOException {
        elasticSearchService.registerDataTransformer(EventData.class, new EventDataTransformer());
        elasticSearchService.deleteIndex(EventData.class);
        elasticSearchService.createIndex(EventData.class);
    }

    @Test
    public void testGetNotExistingDocument() throws IOException, DataMappingException {
        Optional<EventData> document = elasticSearchService.getDocumentById(EventData.class, new DocumentId("not-existing-id"));
        Assert.assertTrue(document.isEmpty());
    }

    @Test
    public void testCreateReadDeleteDocument() throws IOException, DataMappingException {
        boolean result = false;
        Optional<EventData> documentById = null;
        result = elasticSearchService.saveDocument(EventData.class, eventData);
        Assert.assertTrue(result);
        result = elasticSearchService.flushIndex(EventData.class);
        Assert.assertTrue(result);
        documentById = elasticSearchService.getDocumentById(EventData.class, TestUtils.createDocumentId(eventData.getId()));
        EventData eventDataFromElastic = documentById.get();
        Assert.assertTrue(documentById.isPresent());
        Assert.assertEquals(eventData, eventDataFromElastic);
        result = elasticSearchService.deleteDocumentById(EventData.class, TestUtils.createDocumentId(eventData.getId()));
        Assert.assertTrue(result);
        documentById = elasticSearchService.getDocumentById(EventData.class, TestUtils.createDocumentId(eventData.getId()));
        Assert.assertTrue(documentById.isEmpty());
    }

    @Test
    public void testCreateGetDeleteDocuments() throws IOException, InterruptedException {
        boolean result = false;
        int SIZE = 100;
        EventData[] eventData = new EventData[SIZE];
        TestObserver testObserver = new TestObserver();
        for (int i = 0; i < SIZE; i++) {
            eventData[i] = TestUtils.createEventData(i);
        }

        result = elasticSearchService.flushIndex(EventData.class);
        Assert.assertTrue(result);

        //TODO: fix transactional behavior
        Thread.sleep(30_000);

        for (int i = 0; i < SIZE; i++) {
            result = elasticSearchService.saveDocument(EventData.class, eventData[i]);
            Assert.assertTrue(result);
        }

        //TODO: fix transactional behavior
        Thread.sleep(30_000);

        elasticSearchService.getDocuments(EventData.class, testObserver, 20);
        testObserver.await(1, TimeUnit.MINUTES);

        Assert.assertTrue(testObserver.isFinished());
        Assert.assertNotNull(testObserver.getDisposable());
        Assert.assertEquals(testObserver.getErrors().size(), 0);
        Assert.assertEquals(testObserver.getDocs().size(), SIZE);

        int matchedCounter = 0;
        for (int i = 0; i < SIZE; i++) {
            for (EventData data: testObserver.getDocs()) {
                if (data.getId().equals(eventData[i].getId())) {
                    matchedCounter++;
                    Assert.assertEquals(data, eventData[i]);
                    break;
                }
            }
        }
        Assert.assertEquals(matchedCounter, SIZE);
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

    public static void main(String[] args) throws Exception {
        ElasticSearchServiceDocumentsTestsIT testsIT = new ElasticSearchServiceDocumentsTestsIT();
        testsIT.init();
        testsIT.beforeMethod();
        testsIT.testCreateGetDeleteDocuments();
        testsIT.afterMethod();
        testsIT.shutdown();
    }

}
