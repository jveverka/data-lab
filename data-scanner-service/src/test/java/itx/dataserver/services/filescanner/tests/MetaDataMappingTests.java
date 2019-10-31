package itx.dataserver.services.filescanner.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataInfo;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MetaDataMappingTests {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test(enabled = false)
    public void testMapping001() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/metadata/meta-data-001.json");
        UnmappedData unmappedData = objectMapper.readValue(is, UnmappedData.class);
        MetaData model = ParsingUtils.readFromJsonString(unmappedData.getJsonData());
        Optional<MetaDataInfo> metaDataInfo = DataUtils.createMetaDataInfo(unmappedData.getId(), model);

        Assert.assertNotNull(metaDataInfo);
        Assert.assertTrue(metaDataInfo.isPresent());
    }

}
