package hasher;

import java.io.IOException;
import java.io.InputStream;

public interface Hasher {
    byte[] makeHash(InputStream stream) throws IOException;
}
