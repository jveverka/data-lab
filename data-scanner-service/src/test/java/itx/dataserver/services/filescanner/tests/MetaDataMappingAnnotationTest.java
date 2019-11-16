package itx.dataserver.services.filescanner.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaData;
import itx.dataserver.services.filescanner.dto.metadata.annotation.AnnotationMetaDataTransformer;
import itx.elastic.service.DataMappingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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

    @Test
    public void testAnnotationDataTransformer() throws IOException, DataMappingException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/annotation-meta-data-001.json");
        AnnotationMetaData annotationMetaData = objectMapper.readValue(is, AnnotationMetaData.class);
        annotationMetaData = AnnotationMetaData.from(new FileInfoId("xxxx"), annotationMetaData);
        AnnotationMetaDataTransformer transformer = new AnnotationMetaDataTransformer();

        Map<String, Object> source = transformer.getSource(annotationMetaData);
        Assert.assertNotNull(source);

        AnnotationMetaData transformedInstance = transformer.getInstance(transformer.getDocumentId(annotationMetaData), source);
        Assert.assertNotNull(transformedInstance);

        Assert.assertNotNull(transformedInstance.getPath());
        Assert.assertEquals(transformedInstance.getPath().toString(), "/datapool/photos/event/image-001.jpg");
        Assert.assertEquals(transformedInstance.getName(), "hiking");
        Assert.assertEquals(transformedInstance.getDescription(), "hiking in slovakia");
        Assert.assertNotNull(transformedInstance.getCoordinates());
        Assert.assertTrue(transformedInstance.getCoordinates().getLon() == 42.1518F);
        Assert.assertTrue(transformedInstance.getCoordinates().getLat() == 18.5463F);
    }

}