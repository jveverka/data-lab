package itx.image.service.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import itx.image.service.model.MetaData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

public class MetaDataMappingTests {

    @DataProvider(name = "mappingTestProvider")
    public static Object[][] getMetaDataPaths() {
        return new Object[][] {
                { "/data/MetaData-01.json" },
                { "/data/MetaData-02.json" },
                { "/data/MetaData-03.json" }
        };
    }

    @Test(dataProvider = "mappingTestProvider")
    public void mappingTest(String metaDataPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream data = this.getClass().getResourceAsStream(metaDataPath);
        MetaData metaData = mapper.readValue(data, MetaData.class);
        Assert.assertNotNull(metaData);
    }

}
