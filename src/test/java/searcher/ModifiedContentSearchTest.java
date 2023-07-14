package searcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModifiedContentSearchTest {
    public void setUp(String test_dir) throws Exception {
        Path testDirPath = Files.createDirectory(Path.of(test_dir));
        Files.createDirectory(Path.of(test_dir, "dir2"));
        Files.createDirectory(Path.of(test_dir, "dir3"));

        Files.createFile(Path.of(test_dir, "oldFile.txt"));
        Files.createFile(Path.of(test_dir, "dir2", "test2.txt"));
        Files.createFile(Path.of(test_dir, "dir3", "test3.txt"));
    }

    public void tearDown(String test_dir) throws Exception {
        deleteDirectory(Path.of(test_dir).toFile());
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
    public void testModifiedContentSearch() throws Exception {
        final String test_dir = "getFileListTestDir";
        setUp(test_dir);
        TimeUnit.SECONDS.sleep(1);
        File pivotFile = File.createTempFile("pivotFile", ".tmp");
        long pivotTime = pivotFile.lastModified();
        TimeUnit.SECONDS.sleep(1);

        Path expectedNewFile1 = Files.createFile(Path.of(test_dir, "newFile1.txt"));
        Path expectedNewFile2 = Files.createFile(Path.of(test_dir, "dir2/newFile2.txt"));
        Files.deleteIfExists(Path.of(test_dir, "dir2", "test2.txt"));

        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(test_dir, pivotTime);
        List<String> filePaths = modifiedContentSearch.getFilePaths();
        List<String> dirPaths = modifiedContentSearch.getDirPaths();

        assertTrue(filePaths.contains(expectedNewFile1.toAbsolutePath().toString()));
        assertTrue(filePaths.contains(expectedNewFile2.toAbsolutePath().toString()));

        assertTrue(dirPaths.contains(Path.of(test_dir, "dir2").toAbsolutePath().toString()));

        tearDown(test_dir);
    }


    @Test
    public void countDirContentsTest() throws Exception {
        final String testDir = "countDirContentsTestDir";
        setUp(testDir);
        Path testDirPath = Path.of(testDir);
        
        int count = ModifiedContentSearch.countDirContents(testDir);
        assert(count == 3);

        deleteDirectory(testDirPath.toFile());
    }
}

