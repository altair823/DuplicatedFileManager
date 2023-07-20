package dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DirMetadataDto {

    private final Connection connection;

    /**
     * Table name for FileMetadata.
     */
    public static final String DIR_TB_NAME = "dir_metadata";

    public DirMetadataDto(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insert metadata into the database.
     * @param dirMetadata metadata to insert
     * @throws SQLException if a database access error occurs
     */
    public void insert(DirMetadata dirMetadata) throws SQLException {
        String insertQuery = "INSERT INTO " + DIR_TB_NAME + " (path, last_modified, content_count) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, dirMetadata.path());
            pstmt.setLong(2, dirMetadata.lastModified());
            pstmt.setLong(3, dirMetadata.contentCount());
            pstmt.executeUpdate();
        }
    }

    /**
     * Get all directory path list from the database.
     * @return list of file path
     * @throws SQLException if a database access error occurs
     */
    public List<String> getAllPath() throws SQLException {
        String selectQuery = "SELECT path FROM " + DIR_TB_NAME;
        List<String> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("path"));
            }
        }
        return result;
    }

    public List<DirMetadata> getAll() throws SQLException {
        String selectQuery = "SELECT * FROM " + DIR_TB_NAME;
        List<DirMetadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new DirMetadata(
                        rs.getString("path"),
                        rs.getLong("last_modified"),
                        rs.getLong("content_count")
                ));
            }
        }
        return result;
    }
}
