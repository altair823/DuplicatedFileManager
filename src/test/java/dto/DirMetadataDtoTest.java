package dto;

import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dto.DirMetadataDto.DIR_TB_NAME;

class DirMetadataDtoTest {

    private Connection connection;
    private DirMetadataDto dirMetadataDto;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        dirMetadataDto = new DirMetadataDto(connection);
        String createTableQuery = "CREATE TABLE " + DIR_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL, " +
                "last_modified BIGINT NOT NULL, " +
                "content_count INT NOT NULL);";
        // 테이블 생성
        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
        }
    }

    @AfterEach
    public void teardown() throws SQLException {
        String dropTableQuery = "DROP TABLE IF EXISTS " + DIR_TB_NAME + ";";
        try (PreparedStatement pstmt = connection.prepareStatement(dropTableQuery)) {
            pstmt.execute();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void getAllPathTest() throws SQLException {
        DirMetadata dirMetadata1 = new DirMetadata(
                Path.of("Users/John/Desktop"),
                1234567890,
                23
        );
        dirMetadataDto.insert(dirMetadata1);
        DirMetadata dirMetadata2 = new DirMetadata(
                Path.of("Users/John/Desktop/test"),
                987654321,
                56
        );
        dirMetadataDto.insert(dirMetadata2);

        List<String> allPath = dirMetadataDto.getAllPath();
        Assertions.assertEquals(2, allPath.size());
        Assertions.assertEquals(dirMetadata1.path().toString(), allPath.get(0));
        Assertions.assertEquals(dirMetadata2.path().toString(), allPath.get(1));
    }

    @Test
    void getAllTest() throws SQLException {
        DirMetadata dirMetadata1 = new DirMetadata(
                Path.of("Users/John/Desktop"),
                1234567890,
                23
        );
        dirMetadataDto.insert(dirMetadata1);
        DirMetadata dirMetadata2 = new DirMetadata(
                Path.of("Users/John/Desktop/test"),
                987654321,
                56
        );
        dirMetadataDto.insert(dirMetadata2);

        List<DirMetadata> all = dirMetadataDto.getAll();
        Assertions.assertEquals(2, all.size());
        Assertions.assertEquals(dirMetadata1, all.get(0));
        Assertions.assertEquals(dirMetadata2, all.get(1));
    }
}