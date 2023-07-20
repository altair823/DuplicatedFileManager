package dto;

import java.util.Objects;

/**
 * Record for storing file information.
 */
public record FileMetadata(
        String path,
        long lastModified,
        long size,
        String hash
) {
    /**
     * Constructor for FileMetadata.
     * @param path path of the file
     * @param lastModified last modified time of the file
     * @param size size of the file
     * @param hash hash of the file
     */
    public FileMetadata {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        if (lastModified < 0) {
            throw new IllegalArgumentException("lastModified cannot be negative");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
        if (hash == null) {
            throw new IllegalArgumentException("hash cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileMetadata fileMetadata)) return false;
        return lastModified == fileMetadata.lastModified && size == fileMetadata.size && Objects.equals(path, fileMetadata.path) && Objects.equals(hash, fileMetadata.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, lastModified, size, hash);
    }
}
