package itx.dataserver.services.filescanner.tests;

import com.drew.imaging.ImageProcessingException;
import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MetaDataMappingTests {

    @Test(enabled = false)
    public void testMapping001() throws ImageProcessingException, IOException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/meta-data-001.json");
        MetaData model = ParsingUtils.createModel(is);
        FileInfoId id = new FileInfoId("001");
        Optional<MetaDataInfo> metaDataInfo = DataUtils.createMetaDataInfo(id, model);

        Assert.assertNotNull(metaDataInfo);
    }

}
