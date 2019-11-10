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

public class MetaDataMappingImageTests {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "testMappingData")
    public static Object[][] testMappingData() {
        return new Object[][] {
                { "/metadata/image-meta-data-000.json", Boolean.TRUE, "2019-09-30 22:09:54", Boolean.TRUE },
                { "/metadata/image-meta-data-001.json", Boolean.TRUE, "2019-08-27 13:51:06", Boolean.TRUE },
                { "/metadata/image-meta-data-003.json", Boolean.FALSE, null,  Boolean.FALSE },
        };
    }

    @Test(dataProvider = "testMappingData")
    public void testMapping(String unmappedDataResourcePath, boolean expectGps, String expectedTimeStamp, boolean expectVendorData) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(unmappedDataResourcePath);
        UnmappedData unmappedData = objectMapper.readValue(is, UnmappedData.class);
        MetaData model = ParsingUtils.readFromJsonString(unmappedData.getJsonData());
        Optional<ImageMetaDataInfo> metaDataInfoOptional = DataUtils.createImageMetaDataInfo(unmappedData.getId(), model);

        Assert.assertNotNull(metaDataInfoOptional);
        Assert.assertTrue(metaDataInfoOptional.isPresent());

        ImageMetaDataInfo imageMetaDataInfo = metaDataInfoOptional.get();

        Assert.assertNotNull(imageMetaDataInfo);
        Assert.assertEquals(imageMetaDataInfo.getImageType(), "jpeg");

        if (expectedTimeStamp != null) {
            Assert.assertNotNull(imageMetaDataInfo.getTimeStamp());
            Assert.assertEquals(imageMetaDataInfo.getTimeStamp(), expectedTimeStamp);
        } else {
            Assert.assertNull(imageMetaDataInfo.getTimeStamp());
        }

        if (expectGps) {
            Assert.assertNotNull(imageMetaDataInfo.getGps());
            Assert.assertNotNull(imageMetaDataInfo.getGps().getTimeStamp());
            Assert.assertNotNull(imageMetaDataInfo.getGps().getCoordinates());
            Assert.assertNotNull(imageMetaDataInfo.getGps().getProcessingMethod());
            Assert.assertNotNull(imageMetaDataInfo.getGps().getTimeStamp());
        } else {
            Assert.assertNull(imageMetaDataInfo.getGps());
        }

        if (expectVendorData) {
            Assert.assertNotNull(imageMetaDataInfo.getDeviceInfo());
            Assert.assertNotNull(imageMetaDataInfo.getDeviceInfo().getModel());
            Assert.assertNotNull(imageMetaDataInfo.getDeviceInfo().getVendor());
        } else {

        }

        Assert.assertTrue(imageMetaDataInfo.getImageHeight() > 0);
        Assert.assertTrue(imageMetaDataInfo.getImageWidth() > 0);
    }

}
