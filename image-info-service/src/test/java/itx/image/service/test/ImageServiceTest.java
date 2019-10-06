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
    public static Object[][] primeNumbers() {
        return new Object[][] {
                { "/IMG_20180827_190350.jpg" },
                { "/20190930_220954.jpg" },
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
        String jsonData = ParsingUtils.printToJson(metaDataOptional.get());
        Assert.assertNotNull(jsonData);
    }

    @Test
    public void testNoMetaDataRead() {
        InputStream imageStream = this.getClass().getResourceAsStream("/TEXT-FILE.txt");
        ImageService imageService = new ImageServiceImpl();
        Optional<MetaData> metaDataOptional = imageService.getMetaData(imageStream);
        Assert.assertNotNull(metaDataOptional);
        Assert.assertTrue(metaDataOptional.isEmpty());
    }

}
