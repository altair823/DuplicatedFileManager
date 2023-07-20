package dto;

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
     * @throws SQLException if a database access error occurs
     */
    public void insert(FileMetadata FileMetadata) throws SQLException {
        String insertQuery = "INSERT INTO " + FILE_TB_NAME + " (path, last_modified, size, hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, FileMetadata.path());
            pstmt.setLong(2, FileMetadata.lastModified());
            pstmt.setLong(3, FileMetadata.size());
            pstmt.setString(4, FileMetadata.hash());
            pstmt.executeUpdate();
        }
    }

    /**
     * Get all file path list from the database.
     * @return list of file path
     * @throws SQLException if a database access error occurs
     */
    public List<String> getAllPath() throws SQLException {
        String selectQuery = "SELECT path FROM " + FILE_TB_NAME;
        List<String> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("path"));
            }
        }
        return result;
    }

    /**
     * Get all metadata list from the database.
     * @return list of metadata
     * @throws SQLException if a database access error occurs
     */
    public List<FileMetadata> getAll() throws SQLException {
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
        }
        return result;
    }
    
    /**
     * Delete metadata from the database.
     * @param path file path to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteByPath(String path) throws SQLException {
        String deleteQuery = "DELETE FROM " + FILE_TB_NAME + " WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, path);
            pstmt.executeUpdate();
        }
    }

    /**
     * Search metadata from the database by file path.
     * @param path file path to search
     * @return list of metadata
     * @throws SQLException if a database access error occurs
     */
    public List<FileMetadata> searchByPath(String path) throws SQLException {
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
        }
        return result;
    }

    /**
     * Search metadata from the database by hash.
     * @param hash hash to search
     * @return list of metadata
     * @throws SQLException if a database access error occurs
     */
    public List<FileMetadata> searchByHash(String hash) throws SQLException {
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
        }
        return result;
    }
}
