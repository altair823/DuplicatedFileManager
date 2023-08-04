package dto.metadata.file;

import model.hasher.Hasher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    /**
     * Create FileMetadata object.
     * @param path path of the file
     * @return FileMetadata object
     */
    public static FileMetadata create(String path, Hasher hasher) {
        try {
            return new FileMetadata(
                    path,
                    getActualFileModifiedTime(path),
                    getActualFileSize(path),
                    hasher.makeHash(new FileInputStream(path))
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get actual file hash.
     * @param path path of the file
     * @return actual file hash
     */
    public static long getActualFileModifiedTime(String path) {
        try {
            return java.nio.file.Files.getLastModifiedTime(java.nio.file.Path.of(path)).toMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Get actual file size.
     * @param path path of the file
     * @return actual file size
     */
    public static long getActualFileSize(String path) {
        try {
            return java.nio.file.Files.size(java.nio.file.Path.of(path));
        } catch (Exception e) {
            return -1;
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

    @Override
    public String toString() {
        return "FileMetadata{" +
                "path='" + path + '\'' +
                ", lastModified=" + lastModified +
                ", size=" + size +
                ", hash='" + hash + '\'' +
                '}';
    }
}
