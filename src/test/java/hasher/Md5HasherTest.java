package hasher;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Md5HasherTest {

    @Test
    void makeHashFromStringTest() throws Exception {
        final String testString = "Hello, World! \nMy name is Hong Gil Dong.";
        MessageDigest controlMd5Hasher = MessageDigest.getInstance("MD5");
        controlMd5Hasher.update(testString.getBytes());
        byte[] expectedDigest = controlMd5Hasher.digest();
        Hasher md5Hasher = new Md5Hasher();
        byte[] testedDigest = md5Hasher.makeHash(new ByteArrayInputStream(testString.getBytes()));

        assert(Arrays.equals(expectedDigest, testedDigest));
    }

}