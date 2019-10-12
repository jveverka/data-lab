package itx.fs.service.test;

import itx.fs.service.FSUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.Optional;

public class FileExtensionTests {

    @DataProvider(name = "testFilePathProvider")
    public static Object[][] getVideoPaths() {
        return new Object[][] {
                { Path.of("/media/GH010624.MP4"), Boolean.TRUE, Optional.of("MP4") },
                { Path.of("/media/GH010624.THM"), Boolean.TRUE, Optional.of("THM") },
                { Path.of("/root/media/Untitled-Diagram.png"), Boolean.TRUE, Optional.of("png") },
                { Path.of("/root/media/some file.png"), Boolean.TRUE, Optional.of("png") },
                { Path.of("/root/media/some file .png"), Boolean.TRUE, Optional.of("png") },
                { Path.of("/root/media/some.file.png"), Boolean.TRUE, Optional.of("png") },
                { Path.of("/root/media/"), Boolean.TRUE, Optional.empty() },
                { Path.of("/root/media"), Boolean.TRUE, Optional.empty() },
                { Path.of("/root/media/somefile."), Boolean.TRUE, Optional.empty() },
                { Path.of("/root/media.txt"), Boolean.FALSE, Optional.empty() },
                { Path.of("/root/media.txt"), Boolean.TRUE, Optional.of("txt") },
        };
    }

    @Test(dataProvider = "testFilePathProvider")
    public void testFileExtension(Path path, Boolean isRegularFile, Optional<String> expectedExtension) {
        Optional<String> extension = FSUtils.getFileExtension(path, isRegularFile);
        Assert.assertNotNull(extension);
        Assert.assertEquals(extension.isPresent(), expectedExtension.isPresent());
        if (extension.isPresent()) {
            Assert.assertEquals(extension.get(), expectedExtension.get());
        }
    }

}
