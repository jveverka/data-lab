package itx.image.service.test;

import com.drew.imaging.ImageProcessingException;
import itx.image.service.ImageService;
import itx.image.service.ImageServiceImpl;
import itx.image.service.dto.ExifInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class ImageServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(ImageServiceTest.class);

    @Test
    public void testExifDataRead() throws ImageProcessingException, IOException {
        final String imagePath = "/IMG_20180827_190350.jpg";
        LOG.info("reading image {}", imagePath);
        InputStream imageStream = this.getClass().getResourceAsStream(imagePath);
        ImageService imageService = new ImageServiceImpl();

        Optional<ExifInfo> exifInfo = imageService.getExifInfo(imageStream);
        Assert.assertNotNull(exifInfo);
        Assert.assertTrue(exifInfo.isPresent());
        Assert.assertTrue(exifInfo.get().getWidth() == 2448);
        Assert.assertTrue(exifInfo.get().getHeight() == 3264);
    }

}
