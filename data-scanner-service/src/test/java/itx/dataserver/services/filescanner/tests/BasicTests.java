package itx.dataserver.services.filescanner.tests;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoDataTransformer;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfoTransformer;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class BasicTests {

    @Test
    public static void testFileInfoDataTransformer() throws IOException {
        FileInfoDataTransformer transformer = new FileInfoDataTransformer();
        XContentBuilder indexMapping = transformer.getIndexMapping();
        Assert.assertNotNull(indexMapping);
        Assert.assertNotNull(transformer.getIndexName());
    }

    @Test
    public static void testMetaDataInfoTransformer() throws IOException {
        MetaDataInfoTransformer transformer = new MetaDataInfoTransformer();
        XContentBuilder indexMapping = transformer.getIndexMapping();
        Assert.assertNotNull(indexMapping);
        Assert.assertNotNull(transformer.getIndexName());
    }

}
