package dao;

import model.metadata.FileMetadata;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dao.FileMetadataDao.FILE_TB_NAME;

public class FileMetadataDaoTest {

    private Connection connection;
    private FileMetadataDao fileMetadataDao;
    FileMetadata fileMetadata1;
    FileMetadata fileMetadata2;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        fileMetadataDao = new FileMetadataDao(connection);
        String createTableQuery = "CREATE TABLE " + FILE_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL UNIQUE, " +
                "last_modified BIGINT NOT NULL, " +
                "size BIGINT NOT NULL, " +
                "hash VARCHAR(64) NOT NULL);";

        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
        }

        fileMetadata1 = new FileMetadata(
                "Users/John/Desktop/test.txt",
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileMetadataDao.insert(fileMetadata1);

        fileMetadata2 = new FileMetadata(
                "Users/John/Desktop/test2.txt",
                987654321,
                987654321,
                "fedcba0987654321"
        );
        fileMetadataDao.insert(fileMetadata2);
    }

    @AfterEach
    public void teardown() throws SQLException {
        String dropTableQuery = "DROP TABLE IF EXISTS " + FILE_TB_NAME + ";";
        try (PreparedStatement pstmt = connection.prepareStatement(dropTableQuery)) {
            pstmt.execute();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void getAllPathTest() {
        List<String> result = fileMetadataDao.getAllPath();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(fileMetadata1.path(), result.get(0));
        Assertions.assertEquals(fileMetadata2.path(), result.get(1));
    }

    @Test
    void getAllTest() {
        List<FileMetadata> result = fileMetadataDao.getAll();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        Assertions.assertEquals(fileMetadata2, result.get(1));
    }
    
    @Test
    void searchByPathTest() {
        List<FileMetadata> result = fileMetadataDao.searchByPath(fileMetadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        List<FileMetadata> result2 = fileMetadataDao.searchByPath(fileMetadata2.path());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(fileMetadata2, result2.get(0));
    }

    @Test
    void searchByHashTest() {
        List<FileMetadata> result = fileMetadataDao.searchByHash(fileMetadata1.hash());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        List<FileMetadata> result2 = fileMetadataDao.searchByHash(fileMetadata2.hash());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(fileMetadata2, result2.get(0));
        List<FileMetadata> result3 = fileMetadataDao.searchByHash("1234567890abcdef1234567890abcdef");
        Assertions.assertEquals(0, result3.size());
    }

    @Test
    void updateLastModifiedTest() {
        long newLastModified = 666666666;
        fileMetadataDao.updateLastModified(fileMetadata1.path(), newLastModified);
        List<FileMetadata> result = fileMetadataDao.searchByPath(fileMetadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(newLastModified, result.get(0).lastModified());
    }

    @Test
    void updateSizeTest() {
        long newSize = 444444444;
        fileMetadataDao.updateSize(fileMetadata1.path(), newSize);
        List<FileMetadata> result = fileMetadataDao.searchByPath(fileMetadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(newSize, result.get(0).size());
    }

    @Test
    void updateHashTest() {
        String newHash = "thisistesthash";
        fileMetadataDao.updateHash(fileMetadata1.path(), newHash);
        List<FileMetadata> result = fileMetadataDao.searchByPath(fileMetadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(newHash, result.get(0).hash());
    }
}
