package searcher;

import java.io.File;
import java.util.*;

public class ModifiedContentSearch {

    private final List<String> dirPaths;
    private final List<String> filePaths;

    public ModifiedContentSearch(String rootPath, long timestamp) {
        this.dirPaths = new ArrayList<>();
        this.filePaths = new ArrayList<>();
        File root = new File(rootPath);
        searchByTime(root, timestamp);
    }

    private void searchByTime(File currentDir, long timestamp) {
        File[] files = currentDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            long lastModified = file.lastModified();
            if (file.isDirectory()) {
                if (lastModified > timestamp) {
                    this.dirPaths.add(file.getAbsolutePath());
                }
                searchByTime(file, timestamp);
            } else {
                if (lastModified > timestamp) {
                    this.filePaths.add(file.getAbsolutePath());
                }
            }
        }
    }

    public List<String> getDirPaths() {
        return dirPaths;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public static int countDirContents(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return 0;
        }

        File[] contents = folder.listFiles();
        if (contents == null) {
            return 0;
        }

        int count = 0;
        for (File file : contents) {
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                count += countDirContents(file.getAbsolutePath());
            }
        }

        return count;
    }
}