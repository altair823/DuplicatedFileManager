package hasher;

import org.junit.jupiter.api.Assertions;
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
        String expectedDigest = Arrays.toString(controlMd5Hasher.digest());
        Md5Hasher md5Hasher = new Md5Hasher();
        String testedDigest = md5Hasher.makeHash(new ByteArrayInputStream(testString.getBytes()));

        Assertions.assertEquals(expectedDigest, testedDigest);
    }

    @Test
    void makeHachFromFileTest() throws Exception {
        final String testFileName = "makeHashFromFileTest.txt";
        final String testString = "Hello, World! \nMy name is Hong Gil Dong.";
        BufferedWriter testWriter = new BufferedWriter(new FileWriter(testFileName));
        testWriter.write(testString);
        testWriter.close();

        byte[] buffer = new byte[testString.length()];
        FileInputStream fileInputStream1 = new FileInputStream(testFileName);
        fileInputStream1.read(buffer);
        MessageDigest controlMd5Hasher = MessageDigest.getInstance("MD5");
        String expectedDigest = Arrays.toString(controlMd5Hasher.digest(buffer));
        fileInputStream1.close();

        FileInputStream fileInputStream2 = new FileInputStream(testFileName);
        Md5Hasher md5Hasher = new Md5Hasher();
        String testedDigest = md5Hasher.makeHash(fileInputStream2);
        fileInputStream2.close();

        Assertions.assertEquals(expectedDigest, testedDigest);

        File testFile = new File(testFileName);
        testFile.delete();
    }
}