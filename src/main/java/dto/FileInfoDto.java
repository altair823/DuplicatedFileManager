package dto;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO class for FileInfo using SQL database.
 */
public class FileInfoDto {
    private final Connection connection;

    /**
     * Table name for FileInfo.
     */
    public static final String TB_NAME = "file_info";

    /**
     * Constructor for FileInfoDto.
     * @param connection connection to the database
     */
    public FileInfoDto(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insert file information into the database.
     * @param fileInfo file information to insert
     * @throws SQLException if a database access error occurs
     */
    public void insertFileInfo(FileInfo fileInfo) throws SQLException {
        String insertQuery = "INSERT INTO " + TB_NAME + " (path, last_modified, size, hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, fileInfo.path().toString());
            pstmt.setLong(2, fileInfo.lastModified());
            pstmt.setLong(3, fileInfo.size());
            pstmt.setString(4, fileInfo.hash());
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
        List<String> result = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("path"));
            }
        }
        return result;
    }

    /**
     * Get all file information list from the database.
     * @return list of file information
     * @throws SQLException if a database access error occurs
     */
    public List<FileInfo> getAllFileInfo() throws SQLException {
        String selectQuery = "SELECT * FROM " + TB_NAME;
        List<FileInfo> result = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new FileInfo(
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
