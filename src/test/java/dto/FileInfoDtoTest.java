package dto;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dto.FileInfoDto.TB_NAME;

public class FileInfoDtoTest {

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
    private FileInfoDto fileInfoDto;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        fileInfoDto = new FileInfoDto(connection);
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
    public void insertFileInfoTest() throws SQLException {
        FileInfo fileInfo1 = new FileInfo(
                Path.of("C:\\Users\\John\\Desktop\\test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileInfoDto.insertFileInfo(fileInfo1);

        FileInfo fileInfo2 = new FileInfo(
                Path.of("C:\\Users\\John\\Desktop\\test2.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileInfoDto.insertFileInfo(fileInfo2);

        List<String> result = fileInfoDto.getAllFilePath();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("C:\\Users\\John\\Desktop\\test.txt", result.get(0));
        Assertions.assertEquals("C:\\Users\\John\\Desktop\\test2.txt", result.get(1));
    }

    @Test
    void getAllFileInfo() throws SQLException {
        FileInfo fileInfo1 = new FileInfo(
                Path.of("C:\\Users\\John\\Desktop\\test.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileInfoDto.insertFileInfo(fileInfo1);

        FileInfo fileInfo2 = new FileInfo(
                Path.of("C:\\Users\\John\\Desktop\\test2.txt"),
                1234567890,
                1234567890,
                "1234567890abcdef"
        );
        fileInfoDto.insertFileInfo(fileInfo2);

        List<FileInfo> result = fileInfoDto.getAllFileInfo();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(fileInfo1, result.get(0));
        Assertions.assertEquals(fileInfo2, result.get(1));
    }
}
