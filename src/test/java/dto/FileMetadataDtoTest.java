package dto;

import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dto.FileMetadataDto.FILE_TB_NAME;

public class FileMetadataDtoTest {

    private Connection connection;
    private FileMetadataDto fileMetadataDto;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        fileMetadataDto = new FileMetadataDto(connection);
        String createTableQuery = "CREATE TABLE " + FILE_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL, " +
                "last_modified BIGINT NOT NULL, " +
                "size BIGINT NOT NULL, " +
                "hash VARCHAR(64) NOT NULL);";
        // 테이블 생성
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
        }
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
    public void getAllPathTest() throws SQLException {
        FileMetadata fileMetadata1 = new FileMetadata(
                "Users/John/Desktop/test.txt",
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileMetadataDto.insert(fileMetadata1);

        FileMetadata fileMetadata2 = new FileMetadata(
                "Users/John/Desktop/test2.txt",
                987654321,
                987654321,
                "fedcba0987654321"
        );
        fileMetadataDto.insert(fileMetadata2);

        List<String> result = fileMetadataDto.getAllPath();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(fileMetadata1.path(), result.get(0));
        Assertions.assertEquals(fileMetadata2.path(), result.get(1));
    }

    @Test
    void getAllTest() throws SQLException {
        FileMetadata fileMetadata1 = new FileMetadata(
                "Users/John/Desktop/test.txt",
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileMetadataDto.insert(fileMetadata1);

        FileMetadata fileMetadata2 = new FileMetadata(
                "Users/John/Desktop/test2.txt",
                987654321,
                987654321,
                "fedcba0987654321"
        );
        fileMetadataDto.insert(fileMetadata2);

        List<FileMetadata> result = fileMetadataDto.getAll();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        Assertions.assertEquals(fileMetadata2, result.get(1));
    }
    
    @Test
    void searchByPathTest() throws SQLException {
        FileMetadata fileMetadata1 = new FileMetadata(
                "Users/John/Desktop/test.txt",
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileMetadataDto.insert(fileMetadata1);

        FileMetadata fileMetadata2 = new FileMetadata(
                "Users/John/Desktop/test2.txt",
                987654321,
                987654321,
                "fedcba0987654321"
        );
        fileMetadataDto.insert(fileMetadata2);

        List<FileMetadata> result = fileMetadataDto.searchByPath(fileMetadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        List<FileMetadata> result2 = fileMetadataDto.searchByPath(fileMetadata2.path());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(fileMetadata2, result2.get(0));
    }

    @Test
    void searchByHashTest() throws SQLException {
        FileMetadata fileMetadata1 = new FileMetadata(
                "Users/John/Desktop/test.txt",
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileMetadataDto.insert(fileMetadata1);

        FileMetadata fileMetadata2 = new FileMetadata(
                "Users/John/Desktop/test2.txt",
                987654321,
                987654321,
                "fedcba0987654321"
        );
        fileMetadataDto.insert(fileMetadata2);

        List<FileMetadata> result = fileMetadataDto.searchByHash(fileMetadata1.hash());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(fileMetadata1, result.get(0));
        List<FileMetadata> result2 = fileMetadataDto.searchByHash(fileMetadata2.hash());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(fileMetadata2, result2.get(0));
        List<FileMetadata> result3 = fileMetadataDto.searchByHash("1234567890abcdef1234567890abcdef");
        Assertions.assertEquals(0, result3.size());
    }
}
