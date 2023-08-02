package dto.metadata.dir;

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

    /**
     * Constructor for DirMetadataDto.
     * @param connection connection to the database
     */
    public DirMetadataDto(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insert metadata into the database.
     * @param dirMetadata metadata to insert
     */
    public void insert(DirMetadata dirMetadata) {
        String insertQuery = "INSERT INTO " + DIR_TB_NAME + " (path, last_modified, content_count) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, dirMetadata.path());
            pstmt.setLong(2, dirMetadata.lastModified());
            pstmt.setLong(3, dirMetadata.contentCount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all directory path list from the database.
     * @return list of file path
     */
    public List<String> getAllPath() {
        String selectQuery = "SELECT path FROM " + DIR_TB_NAME;
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
     * Get all directory metadata from the database.
     * @return list of directory metadata
     */
    public List<DirMetadata> getAll() {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get directory metadata by path from the database.
     * @param path path of the directory
     * @return list of directory metadata
     */
    public List<DirMetadata> searchByPath(String path) {
        String selectQuery = "SELECT * FROM " + DIR_TB_NAME + " WHERE path LIKE ?";
        List<DirMetadata> result = new LinkedList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, path);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(new DirMetadata(
                        rs.getString("path"),
                        rs.getLong("last_modified"),
                        rs.getLong("content_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Update directory metadata by path.
     * @param path path of the directory
     * @param dirMetadata new directory metadata
     */
    public void updateByPath(String path, DirMetadata dirMetadata) {
        String updateQuery = "UPDATE " + DIR_TB_NAME + " SET path = ?, last_modified = ?, content_count = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, dirMetadata.path());
            pstmt.setLong(2, dirMetadata.lastModified());
            pstmt.setLong(3, dirMetadata.contentCount());
            pstmt.setString(4, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update content count of the directory by path.
     * @param path path of the directory
     * @param currentContentCount new content count of the directory
     */
    public void updateContentCount(String path, long currentContentCount) {
        String updateQuery = "UPDATE " + DIR_TB_NAME + " SET content_count = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, currentContentCount);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update last modified time of the directory by path.
     * @param path path of the directory
     * @param currentLastModified new last modified time of the directory
     */
    public void updateLastModified(String path, long currentLastModified) {
        String updateQuery = "UPDATE " + DIR_TB_NAME + " SET last_modified = ? WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setLong(1, currentLastModified);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
