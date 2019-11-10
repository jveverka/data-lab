package itx.dataserver.services.filescanner.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.dataserver.services.filescanner.DataUtils;
import itx.dataserver.services.filescanner.dto.metadata.video.VideoMetaDataInfo;
import itx.dataserver.services.filescanner.dto.unmapped.UnmappedData;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MetaDataMappingVideoTests {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "testMappingData")
    public static Object[][] testMappingData() {
        return new Object[][] {
                { "/metadata/video-meta-data-000.json" },
        };
    }

    @Test(dataProvider = "testMappingData")
    public void testMapping(String unmappedDataResourcePath) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(unmappedDataResourcePath);
        UnmappedData unmappedData = objectMapper.readValue(is, UnmappedData.class);
        MetaData model = ParsingUtils.readFromJsonString(unmappedData.getJsonData());
        Optional<VideoMetaDataInfo> metaDataInfoOptional = DataUtils.createVideoMetaDataInfo(unmappedData.getId(), model);

        Assert.assertNotNull(metaDataInfoOptional);
        Assert.assertTrue(metaDataInfoOptional.isPresent());

        VideoMetaDataInfo videoMetaDataInfo = metaDataInfoOptional.get();
        Assert.assertNotNull(videoMetaDataInfo);
        Assert.assertEquals(videoMetaDataInfo.getVideoType(), "mp4");
        Assert.assertTrue(videoMetaDataInfo.getDuration() == 38.592F);
        Assert.assertTrue(videoMetaDataInfo.getWidth() == 3840);
        Assert.assertTrue(videoMetaDataInfo.getHeight() == 2160);
        Assert.assertTrue(videoMetaDataInfo.getFrameRate() == 59.94006F);
    }

}
