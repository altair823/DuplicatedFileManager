package dto;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dto.MetadataDto.TB_NAME;

public class MetadataDtoTest {


    public static class H2DatabaseSetup {

        private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        private static final String DB_USER = "sa";
        private static final String DB_PASSWORD = "";

        public static Connection createConnection() throws SQLException {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL(DB_URL);
            dataSource.setUser(DB_USER);
            dataSource.setPassword(DB_PASSWORD);
            return dataSource.getConnection();
        }
    }
    private Connection connection;
    private MetadataDto metadataDto;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        metadataDto = new MetadataDto(connection);
        String createTableQuery = "CREATE TABLE " + TB_NAME +
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
        String dropTableQuery = "DROP TABLE IF EXISTS " + TB_NAME + ";";
        try (PreparedStatement pstmt = connection.prepareStatement(dropTableQuery)) {
            pstmt.execute();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void insertMetadataTest() throws SQLException {
        Metadata metadata1 = new Metadata(
                Path.of("Users/John/Desktop/test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        metadataDto.insertMetadata(metadata1);

        Metadata metadata2 = new Metadata(
                Path.of("Users/John/Desktop/test2.txt"),
                987654321,
                987654321,
                "fedcba0987654321"
        );
        metadataDto.insertMetadata(metadata2);

        List<String> result = metadataDto.getAllFilePath();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(metadata1.path().toString(), result.get(0));
        Assertions.assertEquals(metadata2.path().toString(), result.get(1));
    }

    @Test
    void getAllMetadataTest() throws SQLException {
        Metadata metadata1 = new Metadata(
                Path.of("Users/John/Desktop/test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        metadataDto.insertMetadata(metadata1);

        Metadata metadata2 = new Metadata(
                Path.of("Users/John/Desktop/test2.txt"),
                987654321,
                987654321,
                "fedcba0987654321"
        );
        metadataDto.insertMetadata(metadata2);

        List<Metadata> result = metadataDto.getAllMetadata();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(metadata1, result.get(0));
        Assertions.assertEquals(metadata2, result.get(1));
    }
    
    @Test
    void searchMetadataByPathTest() throws SQLException {
        Metadata metadata1 = new Metadata(
                Path.of("Users/John/Desktop/test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        metadataDto.insertMetadata(metadata1);

        Metadata metadata2 = new Metadata(
                Path.of("Users/John/Desktop/test2.txt"),
                987654321,
                987654321,
                "fedcba0987654321"
        );
        metadataDto.insertMetadata(metadata2);

        List<Metadata> result = metadataDto.searchMetadataByPath(metadata1.path());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(metadata1, result.get(0));
        List<Metadata> result2 = metadataDto.searchMetadataByPath(metadata2.path());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(metadata2, result2.get(0));
    }

    @Test
    void searchMetadataByHashTest() throws SQLException {
        Metadata metadata1 = new Metadata(
                Path.of("Users/John/Desktop/test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        metadataDto.insertMetadata(metadata1);

        Metadata metadata2 = new Metadata(
                Path.of("Users/John/Desktop/test2.txt"),
                987654321,
                987654321,
                "fedcba0987654321"
        );
        metadataDto.insertMetadata(metadata2);

        List<Metadata> result = metadataDto.searchMetadataByHash(metadata1.hash());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(metadata1, result.get(0));
        List<Metadata> result2 = metadataDto.searchMetadataByHash(metadata2.hash());
        Assertions.assertEquals(1, result2.size());
        Assertions.assertEquals(metadata2, result2.get(0));
        List<Metadata> result3 = metadataDto.searchMetadataByHash("1234567890abcdef1234567890abcdef");
        Assertions.assertEquals(0, result3.size());
    }
}
