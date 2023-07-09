package hasher;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;

class Md5HasherTest {

    @Test
    void makeHashFromStringTest() throws Exception {
        final String testString = "Hello, World! \nMy name is Hong Gil Dong.";
        MessageDigest controlMd5Hasher = MessageDigest.getInstance("MD5");
        controlMd5Hasher.update(testString.getBytes());
        byte[] expectedDigest = controlMd5Hasher.digest();
        Hasher hasher = new Md5Hasher();
        byte[] testedDigest = hasher.makeHash(new ByteArrayInputStream(testString.getBytes()));

        assert(Arrays.equals(expectedDigest, testedDigest));
    }

    @Test
    void makeHachFromFileTest() throws Exception {
        final String testFileName = "makeHachFromFileTest.txt";
        final String testString = "Hello, World! \nMy name is Hong Gil Dong.";
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFileName));
        testWriter.write(testString);
        testWriter.close();

        byte[] buffer = new byte[testString.length()];
        FileInputStream fileInputStream1 = new FileInputStream(testFileName);
        fileInputStream1.read(buffer);
        MessageDigest controlMd5Hasher = MessageDigest.getInstance("MD5");
        byte[] expectedDigest = controlMd5Hasher.digest(buffer);
        fileInputStream1.close();

        FileInputStream fileInputStream2 = new FileInputStream(testFileName);
        Hasher hasher = new Md5Hasher();
        byte[] testedDigest = hasher.makeHash(fileInputStream2);
        fileInputStream2.close();

        assert(Arrays.equals(expectedDigest, testedDigest));

        File testFile = new File(testFileName);
        testFile.delete();
    }
}