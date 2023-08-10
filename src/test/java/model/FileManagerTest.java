package model;

import dao.ConfigManager;
import dao.H2DatabaseSetup;
import model.metadata.DirMetadata;
import dao.DirMetadataDao;
import model.metadata.FileMetadata;
import dao.FileMetadataDao;
import model.hasher.Hasher;
import model.hasher.Md5Hasher;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import model.searcher.ModifiedContentSearch;
import model.searcher.TotalSearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import static dao.DirMetadataDao.DIR_TB_NAME;
import static dao.FileMetadataDao.FILE_TB_NAME;

class FileManagerTest {

    private Connection connection;

    DirMetadataDao dirMetadataDao;
    FileMetadataDao fileMetadataDao;

    private static final String TEST_DIR_PATH = "DummyFolder1";
    private static final String TEST_TIMESTAMP_FILE_NAME = "lastRunTimestamp.txt";

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8086")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException, IOException {
        connection = H2DatabaseSetup.createConnection();
        dirMetadataDao = new DirMetadataDao(connection);
        fileMetadataDao = new FileMetadataDao(connection);
        String createDirMetadataTableQuery = "CREATE TABLE " + DIR_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL UNIQUE, " +
                "last_modified BIGINT NOT NULL, " +
                "content_count INT NOT NULL);";

        try (PreparedStatement pstmt = connection.prepareStatement(createDirMetadataTableQuery)) {
            pstmt.execute();
        }
        String createFileMetadataTableQuery = "CREATE TABLE " + FILE_TB_NAME +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "path VARCHAR(255) NOT NULL UNIQUE, " +
                "last_modified BIGINT NOT NULL, " +
                "size BIGINT NOT NULL, " +
                "hash VARCHAR(255) NOT NULL);";
        try (PreparedStatement pstmt = connection.prepareStatement(createFileMetadataTableQuery)) {
            pstmt.execute();
        }


        try {
            createFoldersWithDummyFiles();
            Thread.sleep(1000);
            long startTime = System.currentTimeMillis();
            Thread.sleep(1000);
            addDummyFilesToFolders();

            ConfigManager configManager = new ConfigManager(TEST_TIMESTAMP_FILE_NAME);
            configManager.saveLastRunTimestamp(startTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private static void createFoldersWithDummyFiles() {
        String baseFileName = "DummyFile";

        createFoldersRecursive(Paths.get(TEST_DIR_PATH), 5, 3, baseFileName);
    }

    private static void createFoldersRecursive(Path folderPath, int depth, int numFiles, String baseFileName) {
        try {
            if (!folderPath.toFile().isDirectory()) {
                Files.createDirectory(folderPath);
            }

            for (int i = 1; i <= numFiles; i++) {
                String fileName = baseFileName + i + ".txt";
                Path filePath = folderPath.resolve(fileName);

                if (!filePath.toFile().isFile()) {
//                    String fileContent = generateRandomString();
                    String fileContent = generateNonRandomString();
                    Files.write(filePath, fileContent.getBytes());
                }
            }

            if (depth > 1) {
                for (int i = 1; i <= numFiles; i++) {
                    Path newFolderPath = folderPath.resolve("DummyFolder" + i);
                    createFoldersRecursive(newFolderPath, depth - 1, numFiles, baseFileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addDummyFilesToFolders() {
        String baseFileName = "DummyFileAfter";

        addDummyFilesRecursive(Paths.get(TEST_DIR_PATH), baseFileName);
    }

    private static void addDummyFilesRecursive(Path folderPath, String baseFileName) {
        try {
            for (int i = 1; i <= 3; i++) {
                String fileName = baseFileName + i + ".txt";
                Path filePath = folderPath.resolve(fileName);

                if (!filePath.toFile().isFile()) {
//                    String fileContent = generateRandomString();
                    String fileContent = generateNonRandomString();
                    Files.write(filePath, fileContent.getBytes());
                }
            }

            try (var stream = Files.newDirectoryStream(folderPath)) {
                int i = 0;
                for (Path subPath : stream) {
                    if (Files.isDirectory(subPath)) {
                        if (i == 2) {
                            break;
                        }
                        addDummyFilesRecursive(subPath, baseFileName);
                        i++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(100);
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }

    private static String generateNonRandomString() {
        return String.valueOf('A').repeat(100);
    }

    @AfterEach
    public void teardown() throws SQLException, IOException {
        String dropTableQuery = "DROP TABLE IF EXISTS " + DIR_TB_NAME + ";";
        try (PreparedStatement pstmt = connection.prepareStatement(dropTableQuery)) {
            pstmt.execute();
        }
        dropTableQuery = "DROP TABLE IF EXISTS " + FILE_TB_NAME + ";";
        try (PreparedStatement pstmt = connection.prepareStatement(dropTableQuery)) {
            pstmt.execute();
        }
        if (connection != null) {
            connection.close();
        }

        deleteDirectory(new File(TEST_DIR_PATH));
        Files.deleteIfExists(Paths.get("lastRunTimestamp.txt"));
    }

    public static void deleteDirectory(File file) {
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteDirectory(temp);
            }
        }
        if (!file.delete()) {
            System.err.printf("Unable to delete file or directory : %s%n", file);
        }
    }

    @Test
    void updateModifiedDirTest() {
        ConfigManager configManager = new ConfigManager();
        Hasher hasher = new Md5Hasher();
        FileManager fileManager = new FileManager(configManager, hasher);
        fileManager.updateModifiedContent(TEST_DIR_PATH, dirMetadataDao, fileMetadataDao);
        List<FileMetadata> result = fileManager.getDuplicateFiles();

        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(TEST_DIR_PATH, configManager.getLastRunTimestamp());
        List<String> dirPaths = modifiedContentSearch.getDirPaths();

        List<DirMetadata> expectedDirMetadataList = dirPaths.stream().map(DirMetadata::create).toList();

        List<DirMetadata> dirMetadataList = dirMetadataDao.getAll();
        List<FileMetadata> fileMetadataList = fileMetadataDao.getAll();

        Assertions.assertEquals(expectedDirMetadataList, dirMetadataList);
        String hash = fileMetadataList.get(0).hash(); // There should be only one file in database, and all hash should be the same.
        for (FileMetadata fileMetadata : result) {
            Assertions.assertEquals(fileMetadata.hash(), hash);
        }
    }

    @Test
    void updateAllTest() {
        ConfigManager configManager = new ConfigManager(TEST_TIMESTAMP_FILE_NAME);
        Hasher hasher = new Md5Hasher();
        FileManager fileManager = new FileManager(configManager, hasher);
        fileManager.updateAll(TEST_DIR_PATH, dirMetadataDao, fileMetadataDao);

        TotalSearch totalSearch = new TotalSearch(TEST_DIR_PATH);
        List<String> dirPaths = totalSearch.getDirPaths();
        List<String> filePaths = totalSearch.getFilePaths();

        List<DirMetadata> expectedDirMetadataList = dirPaths.stream().map(DirMetadata::create).toList();
        List<FileMetadata> expectedFileMetadataList = filePaths.stream().map(f -> FileMetadata.create(f, hasher)).toList();

        List<DirMetadata> dirMetadataList = dirMetadataDao.getAll();
        List<FileMetadata> fileMetadataList = fileMetadataDao.getAll();

        Assertions.assertEquals(expectedDirMetadataList, dirMetadataList);
        Assertions.assertEquals(expectedFileMetadataList, fileMetadataList);
    }
}