package itx.elastic.service.tests.it;

import itx.elastic.service.ElasticSearchService;
import itx.elastic.service.ElasticSearchServiceImpl;
import itx.elastic.service.dto.ClientConfig;
import itx.elastic.service.dto.DocumentId;
import itx.elastic.service.tests.it.dto.EventData;
import itx.elastic.service.tests.it.dto.EventDataTransformer;
import itx.elastic.service.tests.it.dto.Location;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        eventData = EventData.from("id1", "name1", "description1", ZonedDateTime.now(), 3600, new Location(45.2F, 15.3F));
    }

    @BeforeMethod
    public void beforeMethod() throws IOException {
        elasticSearchService.registerDataTransformer(EventData.class, new EventDataTransformer());
        elasticSearchService.deleteIndex(EventData.class);
        elasticSearchService.createIndex(EventData.class);
    }

    @Test
    public void testGetNotExistingDocument() throws IOException {
        Optional<EventData> document = elasticSearchService.getDocumentById(EventData.class, new DocumentId("not-existing-id"));
        Assert.assertTrue(document.isEmpty());
    }

    @Test
    public void testCreateReadDeleteDocument() throws IOException {
        boolean result = false;
        Optional<EventData> documentById = null;
        result = elasticSearchService.saveDocument(EventData.class, eventData);
        Assert.assertTrue(result);
        documentById = elasticSearchService.getDocumentById(EventData.class, new DocumentId(eventData.getId()));
        Assert.assertTrue(documentById.isPresent());
        result = elasticSearchService.deleteDocumentById(EventData.class, new DocumentId(eventData.getId()));
        Assert.assertTrue(result);
        documentById = elasticSearchService.getDocumentById(EventData.class, new DocumentId(eventData.getId()));
        Assert.assertTrue(documentById.isPresent());
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
