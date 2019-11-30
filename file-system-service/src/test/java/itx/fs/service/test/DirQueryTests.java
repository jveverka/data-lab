package itx.fs.service.test;

import itx.fs.service.dto.DirQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;

public class DirQueryTests {

    @Test
    public void testDirQueryGetPath() {
        Path path = Path.of("/some/path");
        DirQuery dirQuery = new DirQuery(path);
        Assert.assertEquals(dirQuery.getPath(), path);
    }

    @Test
    public void testCreateDirQueryFromPath() {
        Path path = Path.of("/some/path");
        DirQuery dirQuery = DirQuery.create(path);
        Assert.assertEquals(dirQuery.getPath(), path);
    }

    @Test
    public void testCreateDirQueryFromStringPath() {
        Path path = Path.of("/some/path");
        DirQuery dirQuery = DirQuery.create(path.toString());
        Assert.assertEquals(dirQuery.getPath(), path);
    }

}
