package crawler;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

class FileCrawlerTest {

    @Test
    void getFileListTest() throws IOException, InterruptedException {
        final String testDir = "getFileListTestDir";
        final String testFile = "oldFile.txt";
        Path testDirPath = Path.of(testDir);
        if (Files.isDirectory(testDirPath)) {
            deleteDirectory(testDirPath.toFile());
        }
        Files.createDirectory(testDirPath);
        Files.createDirectory(Path.of(testDir, "dir2"));
        Files.createDirectory(Path.of(testDir, "dir3"));
        Path testFilePath = Path.of(testDir, testFile);
        Files.createFile(testFilePath);
        Files.createFile(Path.of(testDir, "dir2", "test2.txt"));
        Files.createFile(Path.of(testDir, "dir3", "test3.txt"));

        FileTime pivotTime = Files.getLastModifiedTime(Path.of(testDir, "dir3", "test3.txt"));
        TimeUnit.SECONDS.sleep(5);

        Path expectedNewFile1 = Path.of(testDir, "newFile1.txt");
        Files.createFile(expectedNewFile1);
        Path expectedNewFile2 = Path.of(testDir, "dir2", "newFile2.txt");
        Files.createFile(expectedNewFile2);

        List<Path> newFileList = FileCrawler.walk(testDirPath, pivotTime);

        assert(newFileList.contains(expectedNewFile1));
        assert(newFileList.contains(expectedNewFile2));

        deleteDirectory(testDirPath.toFile());
    }

    public static void deleteDirectory(File file) {

        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                //recursive delete
                deleteDirectory(temp);
            }
        }
        if (!file.delete()) {
            System.err.printf("Unable to delete file or directory : %s%n", file);
        }
    }
}

