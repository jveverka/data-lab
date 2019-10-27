package itx.dataserver.services.filescanner.tests;

import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.image.service.MediaService;
import itx.image.service.MediaServiceImpl;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Optional;

public class MappingTests {

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
    }

}
