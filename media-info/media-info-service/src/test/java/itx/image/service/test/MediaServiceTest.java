package itx.image.service.test;

import itx.image.service.MediaService;
import itx.image.service.MediaServiceImpl;
import itx.image.service.ParsingUtils;
import itx.image.service.model.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class MediaServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(MediaServiceTest.class);

    private final MediaService mediaService;

    public MediaServiceTest() {
        mediaService = new MediaServiceImpl();
    }

    @DataProvider(name = "testMetaDataReadProvider")
    public static Object[][] getImagePaths() {
        return new Object[][] {
                { "/media/IMG_20180827_190350.jpg" },
                { "/media/20190930_220954.jpg" },
                { "/media/IMG-20171111-WA0007.jpeg" },
        };
    }

    @Test(dataProvider = "testMetaDataReadProvider")
    public void testMetaDataRead(String resourcePath) throws IOException {
        LOG.info("reading image {}", resourcePath);
        InputStream imageStream = this.getClass().getResourceAsStream(resourcePath);
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isPresent());
        Assert.assertTrue(metaDataOptional.get().directoryByName("exif-subifd").isPresent());
        Assert.assertTrue(metaDataOptional.get().directoryByName("exif-ifd0").isPresent());
        String jsonData = ParsingUtils.writeAsJsonString(metaDataOptional.get());
        Assert.assertNotNull(jsonData);
    }

    @DataProvider(name = "testVideoMetaDataReadProvider")
    public static Object[][] getVideoPaths() {
        return new Object[][] {
                { "/media/GH010624.MP4" },
                { "/media/GH010624.THM" },
                { "/media/Untitled-Diagram.png" }
        };
    }

    @Test(dataProvider = "testVideoMetaDataReadProvider")
    public void testVideoMetaDataRead(String resourcePath) throws IOException {
        LOG.info("reading video {}", resourcePath);
        InputStream imageStream = this.getClass().getResourceAsStream(resourcePath);
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isPresent());
    }

    @DataProvider(name = "testNoMetaDataProvider")
    public static Object[][] getPaths() {
        return new Object[][] {
                { "/media/TEXT-FILE.txt" },
                { "/media/GH010624.LRV" },
        };
    }
    @Test(dataProvider = "testNoMetaDataProvider")
    public void testNoMetaDataRead(String resourcePath) {
        InputStream imageStream = this.getClass().getResourceAsStream(resourcePath);
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isEmpty());
    }

    @Test
    public void testGetValueByPath() {
        InputStream imageStream = this.getClass().getResourceAsStream("/media/IMG_20180827_190350.jpg");
        Optional<MetaData> metaDataOptional = mediaService.getMetaData(imageStream);

        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isPresent());

        MetaData metaData = metaDataOptional.get();

        Optional<Long> longValueByPath = metaData.getValueByPath(Long.class, "exif-subifd", "exif-image-height");
        Assert.assertNotNull(longValueByPath);
        Assert.assertTrue(longValueByPath.isPresent());
        Assert.assertTrue(longValueByPath.get() == 3264);

        Optional<String> stringValueByPath = metaData.getValueByPath(String.class, "exif-subifd", "date/time-original");
        Assert.assertNotNull(stringValueByPath);
        Assert.assertTrue(stringValueByPath.isPresent());
        Assert.assertEquals(stringValueByPath.get(), "2018:08:27 19:03:51");

        stringValueByPath = metaData.getValueByPath(String.class, "exif-subifd", "not-existing");
        Assert.assertNotNull(stringValueByPath);
        Assert.assertTrue(stringValueByPath.isEmpty());

        stringValueByPath = metaData.getValueByPath(String.class, "not-existing", "date/time-original");
        Assert.assertNotNull(stringValueByPath);
        Assert.assertTrue(stringValueByPath.isEmpty());

    }

}
