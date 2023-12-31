package dao;

import model.metadata.DirMetadata;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dao.DirMetadataDao.DIR_TB_NAME;

class DirMetadataDaoTest {

    private Connection connection;
    private DirMetadataDao dirMetadataDao;
    DirMetadata dirMetadata1;
    DirMetadata dirMetadata2;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException {
        connection = H2DatabaseSetup.createConnection();
        dirMetadataDao = new DirMetadataDao(connection);
        String createTableQuery = "CREATE TABLE " + DIR_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL UNIQUE, " +
                "last_modified BIGINT NOT NULL, " +
                "content_count INT NOT NULL);";

        try (PreparedStatement pstmt = connection.prepareStatement(createTableQuery)) {
            pstmt.execute();
        }

        dirMetadata1 = new DirMetadata(
                "Users/John/Desktop",
                1234567890,
                23
        );
        dirMetadataDao.insert(dirMetadata1);
        dirMetadata2 = new DirMetadata(
                "Users/John/Desktop/test",
                987654321,
                56
        );
        dirMetadataDao.insert(dirMetadata2);
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
    void getAllPathTest() {

        List<String> allPath = dirMetadataDao.getAllPath();
        Assertions.assertEquals(2, allPath.size());
        Assertions.assertEquals(dirMetadata1.path(), allPath.get(0));
        Assertions.assertEquals(dirMetadata2.path(), allPath.get(1));
    }

    @Test
    void getAllTest() {
        List<DirMetadata> all = dirMetadataDao.getAll();
        Assertions.assertEquals(2, all.size());
        Assertions.assertEquals(dirMetadata1, all.get(0));
        Assertions.assertEquals(dirMetadata2, all.get(1));
    }

    @Test
    void searchByPathTest() {
        List<DirMetadata> searchResult = dirMetadataDao.searchByPath(dirMetadata1.path());
        Assertions.assertEquals(dirMetadata1, searchResult.get(0));
    }

    @Test
    void updateContentCountTest() {
        dirMetadataDao.updateContentCount(dirMetadata1.path(), 100);
        List<DirMetadata> searchResult = dirMetadataDao.searchByPath(dirMetadata1.path());
        Assertions.assertEquals(100, searchResult.get(0).contentCount());
        dirMetadataDao.updateContentCount(dirMetadata2.path(), 0);
        searchResult = dirMetadataDao.searchByPath(dirMetadata2.path());
        Assertions.assertEquals(0, searchResult.get(0).contentCount());
    }

    @Test
    void updateLastModifiedTest() {
        dirMetadataDao.updateLastModified(dirMetadata1.path(), 10000);
        List<DirMetadata> searchResult = dirMetadataDao.searchByPath(dirMetadata1.path());
        Assertions.assertEquals(10000, searchResult.get(0).lastModified());
        dirMetadataDao.updateLastModified(dirMetadata2.path(), 0);
        searchResult = dirMetadataDao.searchByPath(dirMetadata2.path());
        Assertions.assertEquals(0, searchResult.get(0).lastModified());
    }

    @Test
    void insertTest() {
        DirMetadata dirMetadata3 = new DirMetadata(
                "Users/John/Desktop/test2",
                987654321,
                56
        );
        dirMetadataDao.insert(dirMetadata3);
        List<DirMetadata> searchResult = dirMetadataDao.searchByPath(dirMetadata3.path());
        Assertions.assertEquals(dirMetadata3, searchResult.get(0));
    }

    @Test
    void updateByPathTest() {
        DirMetadata dirMetadata3 = new DirMetadata(
                "Users/John/Desktop/test", // same path as dirMetadata2
                28381298, // different last modified
                315135 // different content count
        );
        dirMetadataDao.updateByPath(dirMetadata3.path(), dirMetadata3);
        List<DirMetadata> searchResult = dirMetadataDao.searchByPath(dirMetadata3.path());
        Assertions.assertEquals(dirMetadata3, searchResult.get(0));
    }
}