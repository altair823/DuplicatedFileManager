package hasher;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5 Hasher
 * @see Hasher
 */
public class Md5Hasher implements Hasher {

    /**
     * Buffer size
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Make hash from input stream
     * @param stream input stream
     * @return hash value
     * @throws IOException if I/O error occurs
     */
    static public byte[] makeHash(InputStream stream) throws IOException, NoSuchAlgorithmException {
        final MessageDigest innerHash = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[BUFFER_SIZE];
        int readCount;
        do {
            readCount = stream.read(buffer);
            if (readCount > 0) {
                // If not specify the length of data in buffer,
                // hasher hash total buffer that may contain 0 paddings or garbage data.
                innerHash.update(buffer, 0, readCount);
            }
        } while (readCount != -1);
        stream.close();
        return innerHash.digest();
    }
}
