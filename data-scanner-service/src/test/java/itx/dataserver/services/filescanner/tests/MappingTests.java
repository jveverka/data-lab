package itx.dataserver.services.filescanner.tests;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.image.service.MediaService;
import itx.image.service.MediaServiceImpl;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Optional;

public class MappingTests {

    @Test
    public void testMetaDataMappingNew() {
        InputStream imageStream = this.getClass().getResourceAsStream("/media/20190206_113018.jpg");
        MediaService mediaService = new MediaServiceImpl();
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
    }

    @Test
    public void testMetaDataMapping() {
        InputStream imageStream = this.getClass().getResourceAsStream("/media/20190930_220954.jpg");
        MediaService mediaService = new MediaServiceImpl();
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);
        FileInfoId id = new FileInfoId("123");

        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isPresent());

        Optional<MetaDataInfo> metaDataInfoOptional = DataUtils.createMetaDataInfo(id, metaDataOptional.get());
        Assert.assertNotNull(metaDataInfoOptional);
        Assert.assertTrue(metaDataInfoOptional.isPresent());

        MetaDataInfo metaDataInfo = metaDataInfoOptional.get();
        Assert.assertNotNull(metaDataInfo);
        Assert.assertEquals(metaDataInfo.getImageType(), "jpeg");
        Assert.assertNotNull(metaDataInfo.getGps());
        Assert.assertEquals(metaDataInfo.getTimeStamp(), "2019-09-30 22:09:54");
        Assert.assertEquals(metaDataInfo.getGps().getTimeStamp(), "20190930T200914.000+0000");
    }

    @Test
    public void testDateMapping() throws ParseException {
        Optional<String> result = DataUtils.normalizeDateTime("2019:09:30 22:09:54");
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get(), "2019-09-30 22:09:54");
        result = DataUtils.normalizeDateTime("2019:09:30 20:09:14.000 UTC");
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get(), "2019-09-30 20:09:14");
    }

    @Test
    public void testDateMappingWithTimeZone() {
        Optional<String> result = DataUtils.normalizeDateTimeWithTimeZone("2019:09:30 20:09:14.000 UTC");
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get(), "20190930T200914.000+0000");
        result = DataUtils.normalizeDateTimeWithTimeZone("2019:09:30 20:09:14.012 UTC");
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get(), "20190930T200914.000+0000");
        result = DataUtils.normalizeDateTimeWithTimeZone("2019:09:30 20:09:14.012 CET");
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(result.get(), "20190930T200914.000+0200");
    }

}
