package model.searcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class TotalSearchTest {

    private static final int DEPTH = 3;
    private static final int STRING_LENGTH = 100;
    private static final int DIR_PER_DIR = 3;
    private static final int FILES_PER_DIR = 5;
    private static final String TEST_DIR = "src/test/resources/TotalSearchTestDir";

    @BeforeEach
    void setUp() {
        File testDir = new File(TEST_DIR);
        testDir.mkdirs();
        try {
            createFolderStructure(testDir, DEPTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String generateRandomString() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private static void createFolderStructure(File parentFolder, int depth) throws IOException {
        if (depth == 0) {
            return;
        }

        for (int i = 1; i <= FILES_PER_DIR; i++) {
            String randomString = generateRandomString();
            String fileName = "file" + i + ".txt";
            File file = new File(parentFolder, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(randomString);
            }
        }

        for (int i = 1; i <= DIR_PER_DIR; i++) {
            String folderName = "folder" + i;
            File subFolder = new File(parentFolder, folderName);
            subFolder.mkdirs();
            createFolderStructure(subFolder, depth - 1);
        }
    }

    @AfterEach
    void tearDown() {
        // Delete test directory.
        deleteFolder(new File(TEST_DIR));
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
    @Test
    void TotalSearchConstructorTest() {
        TotalSearch totalSearch = new TotalSearch(TEST_DIR);
        int expectedDirCount =  3 + 9 + 27;
        int expectedFileCount = FILES_PER_DIR * (int) Math.pow(DIR_PER_DIR, DEPTH - 1);
    }
}