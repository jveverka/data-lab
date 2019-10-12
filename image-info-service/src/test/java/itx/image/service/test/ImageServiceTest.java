package itx.image.service.test;

import itx.image.service.ImageService;
import itx.image.service.ImageServiceImpl;
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

public class ImageServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageServiceTest.class);

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
        ImageService imageService = new ImageServiceImpl();
        Optional<MetaData> metaDataOptional = imageService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isPresent());
        Assert.assertTrue(metaDataOptional.get().directoryByName("exif-subifd").isPresent());
        Assert.assertTrue(metaDataOptional.get().directoryByName("exif-ifd0").isPresent());
        String jsonData = ParsingUtils.printToJson(metaDataOptional.get());
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
        ImageService imageService = new ImageServiceImpl();
        Optional<MetaData> metaDataOptional = imageService.getMetaData(imageStream);
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
        ImageService imageService = new ImageServiceImpl();
        Optional<MetaData> metaDataOptional = imageService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isEmpty());
    }

}
