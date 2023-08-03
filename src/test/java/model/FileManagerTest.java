package model;

import dto.ConfigManager;
import dto.H2DatabaseSetup;
import dto.metadata.dir.DirMetadata;
import dto.metadata.dir.DirMetadataDto;
import dto.metadata.file.FileMetadata;
import dto.metadata.file.FileMetadataDto;
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

import static dto.metadata.dir.DirMetadataDto.DIR_TB_NAME;
import static dto.metadata.file.FileMetadataDto.FILE_TB_NAME;

class FileManagerTest {

    private Connection connection;

    DirMetadataDto dirMetadataDto;
    FileMetadataDto fileMetadataDto;

    @BeforeAll
    public static void initWebServer() throws SQLException {
        Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8086")
                .start();
    }

    @BeforeEach
    public void setup() throws SQLException, IOException {
        connection = H2DatabaseSetup.createConnection();
        dirMetadataDto = new DirMetadataDto(connection);
        fileMetadataDto = new FileMetadataDto(connection);
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

            ConfigManager.createNewLastRunTimestamp(startTime);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private static void createFoldersWithDummyFiles() {
        String baseFolderName = "DummyFolder";
        String baseFileName = "DummyFile";

        createFoldersRecursive(Paths.get(baseFolderName + 1), 5, 3, baseFileName);
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
        String baseFolderName = "DummyFolder";
        String baseFileName = "DummyFileAfter";

        addDummyFilesRecursive(Paths.get(baseFolderName + 1), baseFileName);
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

        deleteDirectory(new File("DummyFolder1"));
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
        fileManager.updateModifiedContent("DummyFolder1", dirMetadataDto, fileMetadataDto);
        List<FileMetadata> result = fileManager.getDuplicateFiles();

        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch("DummyFolder1", configManager.getLastRunTimestamp());
        List<String> dirPaths = modifiedContentSearch.getDirPaths();

        List<DirMetadata> expectedDirMetadataList = dirPaths.stream().map(DirMetadata::create).toList();

        List<DirMetadata> dirMetadataList = dirMetadataDto.getAll();
        List<FileMetadata> fileMetadataList = fileMetadataDto.getAll();

        Assertions.assertEquals(expectedDirMetadataList, dirMetadataList);
        String hash = fileMetadataList.get(0).hash(); // There should be only one file in database, and all hash should be the same.
        for (FileMetadata fileMetadata : result) {
            Assertions.assertEquals(fileMetadata.hash(), hash);
        }
    }

    @Test
    void updateAllTest() {
        ConfigManager configManager = new ConfigManager();
        Hasher hasher = new Md5Hasher();
        FileManager fileManager = new FileManager(configManager, hasher);
        fileManager.updateAll("DummyFolder1", dirMetadataDto, fileMetadataDto);

        TotalSearch totalSearch = new TotalSearch("DummyFolder1");
        List<String> dirPaths = totalSearch.getDirPaths();
        List<String> filePaths = totalSearch.getFilePaths();

        List<DirMetadata> expectedDirMetadataList = dirPaths.stream().map(DirMetadata::create).toList();
        List<FileMetadata> expectedFileMetadataList = filePaths.stream().map(f -> FileMetadata.create(f, hasher)).toList();

        List<DirMetadata> dirMetadataList = dirMetadataDto.getAll();
        List<FileMetadata> fileMetadataList = fileMetadataDto.getAll();

        Assertions.assertEquals(expectedDirMetadataList, dirMetadataList);
        Assertions.assertEquals(expectedFileMetadataList, fileMetadataList);
    }
}