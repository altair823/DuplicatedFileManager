package dto;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Record for storing directory information.
 */
public record DirMetadata(
        Path path,
        long lastModified,
        long contentCount
) {
    /**
     * Constructor for DirMetadata.
     * @param path path of the directory
     * @param lastModified last modified time of the directory
     * @param contentCount number of contents in the directory
     */
    public DirMetadata {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        if (lastModified < 0) {
            throw new IllegalArgumentException("lastModified cannot be negative");
        }
        if (contentCount < 0) {
            throw new IllegalArgumentException("contentCount cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirMetadata dirMetadata)) return false;
        return lastModified == dirMetadata.lastModified && contentCount == dirMetadata.contentCount && path.equals(dirMetadata.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, lastModified, contentCount);
    }
}
