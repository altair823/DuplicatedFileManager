package hasher;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Hasher implements Hasher {
    private static final int BUFFER_SIZE = 1024;
    private final MessageDigest innerHash = MessageDigest.getInstance("MD5");

    public Md5Hasher() throws NoSuchAlgorithmException {

    }


    public byte[] makeHash(InputStream stream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int readCount;
        do {
            readCount = stream.read(buffer);
            if (readCount > 0) {
                innerHash.update(buffer, 0, readCount);
                // If not specify the length of data in buffer,
                // hasher hash total buffer that may contain 0 paddings or garbage data.
            }
        } while (readCount != -1);
        stream.close();
        return innerHash.digest();
    }
}
