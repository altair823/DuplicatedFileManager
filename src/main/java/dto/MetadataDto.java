package dto;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * DTO class for Metadata using SQL database.
 */
public class MetadataDto {
    private final Connection connection;

    /**
     * Table name for Metadata.
     */
    public static final String TB_NAME = "file_info";

    /**
     * Constructor for MetadataDto.
     * @param connection connection to the database
     */
    public MetadataDto(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insert metadata into the database.
     * @param Metadata metadata to insert
     * @throws SQLException if a database access error occurs
     */
    public void insertMetadata(Metadata Metadata) throws SQLException {
        String insertQuery = "INSERT INTO " + TB_NAME + " (path, last_modified, size, hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, Metadata.path().toString());
            pstmt.setLong(2, Metadata.lastModified());
            pstmt.setLong(3, Metadata.size());
            pstmt.setString(4, Metadata.hash());
            pstmt.executeUpdate();
        }
    }

    /**
     * Get all file path list from the database.
     * @return list of file path
     * @throws SQLException if a database access error occurs
     */
    public List<String> getAllFilePath() throws SQLException {
        String selectQuery = "SELECT path FROM " + TB_NAME;
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
    public List<Metadata> getAllMetadata() throws SQLException {
        String selectQuery = "SELECT * FROM " + TB_NAME;
        List<Metadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new Metadata(
                        Path.of(rs.getString("path")),
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
    public void deleteMetadata(Path path) throws SQLException {
        String deleteQuery = "DELETE FROM " + TB_NAME + " WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, path.toString());
            pstmt.executeUpdate();
        }
    }

    /**
     * Search metadata from the database by file path.
     * @param path file path to search
     * @return list of metadata
     * @throws SQLException if a database access error occurs
     */
    public List<Metadata> searchMetadataByPath(Path path) throws SQLException {
        String selectQuery = "SELECT * FROM " + TB_NAME + " WHERE path = ?";
        List<Metadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, path.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new Metadata(
                        Path.of(rs.getString("path")),
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
    public List<Metadata> searchMetadataByHash(String hash) throws SQLException {
        String selectQuery = "SELECT * FROM " + TB_NAME + " WHERE hash = ?";
        List<Metadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, hash);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new Metadata(
                        Path.of(rs.getString("path")),
                        rs.getLong("last_modified"),
                        rs.getLong("size"),
                        rs.getString("hash")
                ));
            }
        }
        return result;
    }
}
