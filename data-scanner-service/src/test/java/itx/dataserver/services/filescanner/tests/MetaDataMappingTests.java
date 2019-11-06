package itx.dataserver.services.filescanner.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.metadata.image.ImageMetaDataInfo;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MetaDataMappingTests {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "testMappingData")
    public static Object[][] testMappingData() {
        return new Object[][] {
                { "/metadata/meta-data-000.json" },
                { "/metadata/meta-data-001.json" },
        };
    }

    @Test(dataProvider = "testMappingData")
    public void testMapping(String unmappedDataResourcePath) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(unmappedDataResourcePath);
        UnmappedData unmappedData = objectMapper.readValue(is, UnmappedData.class);
        MetaData model = ParsingUtils.readFromJsonString(unmappedData.getJsonData());
        Optional<ImageMetaDataInfo> metaDataInfoOptional = DataUtils.createImageMetaDataInfo(unmappedData.getId(), model);

        Assert.assertNotNull(metaDataInfoOptional);
        Assert.assertTrue(metaDataInfoOptional.isPresent());

        ImageMetaDataInfo imageMetaDataInfo = metaDataInfoOptional.get();
        Assert.assertNotNull(imageMetaDataInfo);
        Assert.assertEquals(imageMetaDataInfo.getImageType(), "jpeg");
        Assert.assertNotNull(imageMetaDataInfo.getGps());
        Assert.assertNotNull(imageMetaDataInfo.getTimeStamp());
        Assert.assertNotNull(imageMetaDataInfo.getGps().getTimeStamp());

    }

}
