package dto.metadata.file;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * DTO class for FileMetadata using SQL database.
 */
public class FileMetadataDto {
    private final Connection connection;

    /**
     * Table name for FileMetadata.
     */
    public static final String FILE_TB_NAME = "file_metadata";

    /**
     * Constructor for FileMetadataDto.
     * @param connection connection to the database
     */
    public FileMetadataDto(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insert metadata into the database.
     * @param FileMetadata metadata to insert
     */
    public void insert(FileMetadata FileMetadata) {
        String insertQuery = "INSERT INTO " + FILE_TB_NAME + " (path, last_modified, size, hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, FileMetadata.path());
            pstmt.setLong(2, FileMetadata.lastModified());
            pstmt.setLong(3, FileMetadata.size());
            pstmt.setString(4, FileMetadata.hash());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all file path list from the database.
     * @return list of file path
     */
    public List<String> getAllPath() {
        String selectQuery = "SELECT path FROM " + FILE_TB_NAME;
        List<String> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get all metadata list from the database.
     * @return list of metadata
     */
    public List<FileMetadata> getAll() {
        String selectQuery = "SELECT * FROM " + FILE_TB_NAME;
        List<FileMetadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new FileMetadata(
                        rs.getString("path"),
                        rs.getLong("last_modified"),
                        rs.getLong("size"),
                        rs.getString("hash")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * Delete metadata from the database.
     * @param path file path to delete
     */
    public void deleteByPath(String path) {
        String deleteQuery = "DELETE FROM " + FILE_TB_NAME + " WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Search metadata from the database by file path.
     * @param path file path to search
     * @return list of metadata
     */
    public List<FileMetadata> searchByPath(String path) {
        String selectQuery = "SELECT * FROM " + FILE_TB_NAME + " WHERE path = ?";
        List<FileMetadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, path);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new FileMetadata(
                        rs.getString("path"),
                        rs.getLong("last_modified"),
                        rs.getLong("size"),
                        rs.getString("hash")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Search metadata from the database by hash.
     * @param hash hash to search
     * @return list of metadata
     */
    public List<FileMetadata> searchByHash(String hash) {
        String selectQuery = "SELECT * FROM " + FILE_TB_NAME + " WHERE hash = ?";
        List<FileMetadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, hash);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new FileMetadata(
                        rs.getString("path"),
                        rs.getLong("last_modified"),
                        rs.getLong("size"),
                        rs.getString("hash")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Update last modified time of the metadata.
     * @param path file path to update
     * @param newLastModified new last modified time
     */
    public void updateLastModified(String path, long newLastModified) {
        String updateQuery = "UPDATE " + FILE_TB_NAME + " SET last_modified = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, newLastModified);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update size value in the metadata.
     * @param path file path to update
     * @param newSize new size
     */
    public void updateSize(String path, long newSize) {
        String updateQuery = "UPDATE " + FILE_TB_NAME + " SET size = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)){
            pstmt.setLong(1, newSize);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update hash value in the metadata.
     * @param path file path to update
     * @param newHash new hash
     */
    public void updateHash(String path, String newHash) {
        String updateQuery = "UPDATE " + FILE_TB_NAME + " SET hash = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)){
            pstmt.setString(1, newHash);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
