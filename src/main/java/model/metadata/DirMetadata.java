package model.metadata;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Record for storing directory information.
 */
public record DirMetadata(
        String path,
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

    /**
     * Create DirMetadata object.
     * @param path path of the directory
     * @return DirMetadata object
     */
    public static DirMetadata create(String path) {
        return new DirMetadata(
                path,
                getActualDirModifiedTime(path),
                getActualDirContentCount(path)
        );
    }

    /**
     * Get actual directory content count.
     * @param path path of the directory
     * @return actual directory content count
     */
    public static long getActualDirContentCount(String path) {
        try (Stream<Path> contents = java.nio.file.Files.list(Path.of(path))) {
            return contents.count();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Get actual directory modified time.
     * @param path path of the directory
     * @return actual directory modified time
     */
    public static long getActualDirModifiedTime(String path) {
        try {
            return java.nio.file.Files.getLastModifiedTime(Path.of(path)).toMillis();
        } catch (Exception e) {
            return -1;
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
