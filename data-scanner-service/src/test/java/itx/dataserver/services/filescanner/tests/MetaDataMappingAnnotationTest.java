package itx.dataserver.services.filescanner.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MetaDataMappingAnnotationTest {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAnnotationMetaDataDeserialization() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/annotation-meta-data-001.json");
        AnnotationMetaData annotationMetaData = objectMapper.readValue(is, AnnotationMetaData.class);
        Assert.assertNotNull(annotationMetaData);
        Assert.assertNotNull(annotationMetaData.getPath());
        Assert.assertEquals(annotationMetaData.getPath().toString(), "/datapool/photos/event/image-001.jpg");
        Assert.assertEquals(annotationMetaData.getName(), "hiking");
        Assert.assertEquals(annotationMetaData.getDescription(), "hiking in slovakia");
        Assert.assertNotNull(annotationMetaData.getCoordinates());
        Assert.assertTrue(annotationMetaData.getCoordinates().getLon() == 42.1518F);
        Assert.assertTrue(annotationMetaData.getCoordinates().getLat() == 18.5463F);
    }

    @Test
    public void testAnnotationMetaDataDeserializationNoCoordinates() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/annotation-meta-data-002.json");
        AnnotationMetaData annotationMetaData = objectMapper.readValue(is, AnnotationMetaData.class);
        Assert.assertNotNull(annotationMetaData);
        Assert.assertNotNull(annotationMetaData.getPath());
        Assert.assertEquals(annotationMetaData.getPath().toString(), "/datapool/photos/event/image-001.jpg");
        Assert.assertEquals(annotationMetaData.getName(), "hiking");
        Assert.assertEquals(annotationMetaData.getDescription(), "hiking in slovakia");
        Assert.assertNull(annotationMetaData.getCoordinates());
    }

    @Test
    public void testAnnotationMetaDataBulkDeserialization() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/annotation-meta-data-bulk-001.json");
        List<AnnotationMetaData> annotations = objectMapper.readValue(is, new TypeReference<List<AnnotationMetaData>>(){});
        Assert.assertNotNull(annotations);
        Assert.assertTrue(annotations.size() == 3);
    }

}