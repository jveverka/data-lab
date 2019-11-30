package itx.fs.service.test;

import itx.fs.service.dto.CheckSum;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckSumTests {

    @Test
    public void testEquals() {
        CheckSum checkSum01 = new CheckSum("123456", "sha256");
        CheckSum checkSum02 = new CheckSum("123456", "sha256");
        CheckSum checkSum03 = new CheckSum("123458", "sha256");
        CheckSum checkSum04 = new CheckSum("123456", "sha512");

        Assert.assertTrue(checkSum01.equals(checkSum01));
        Assert.assertTrue(checkSum01.equals(checkSum02));

        Assert.assertFalse(checkSum01.equals(checkSum03));
        Assert.assertFalse(checkSum01.equals(checkSum04));

        Assert.assertFalse(checkSum01.equals(null));
        Assert.assertFalse(checkSum01.equals(new Object()));
    }

    @Test
    public void testHashCode() {
        CheckSum checkSum01 = new CheckSum("123456", "sha256");
        CheckSum checkSum02 = new CheckSum("123456", "sha256");
        CheckSum checkSum03 = new CheckSum("123458", "sha256");

        Assert.assertTrue(checkSum01.hashCode() == checkSum02.hashCode());
        Assert.assertFalse(checkSum01.hashCode() == checkSum03.hashCode());
    }

    @Test
    public void testToString() {
        CheckSum checkSum01 = new CheckSum("123456", "sha256");
        Assert.assertNotNull(checkSum01.toString());
    }

}
