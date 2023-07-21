package hasher;

import java.io.InputStream;

/**
 * Hasher interface
 */
public interface Hasher {

    /**
     * Make hash from input stream
     *
     * @param stream input stream
     * @return hash value
     */
    default String makeHash(InputStream stream) {
        return null;
    }
}
