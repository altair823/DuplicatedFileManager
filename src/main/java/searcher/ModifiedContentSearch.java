package searcher;

import java.io.File;
import java.util.*;

/**
 * Search modified contents
 */
public class ModifiedContentSearch {

    /**
     * List of directory paths
     */
    private final List<String> dirPaths;

    /**
     * List of file paths
     */
    private final List<String> filePaths;

    /**
     * Constructor
     * Files and directories modified after the timestamp are searched recursively when this object is created.
     * @param rootPath root path to search
     * @param timestamp timestamp to compare
     */
    public ModifiedContentSearch(String rootPath, long timestamp) {
        this.dirPaths = new ArrayList<>();
        this.filePaths = new ArrayList<>();
        File root = new File(rootPath);
        searchByTime(root, timestamp);
    }

    /**
     * Search by time
     * @param currentDir current directory
     * @param timestamp timestamp to compare
     */
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
                    searchByTime(file, timestamp);
                }
            } else {
                if (lastModified > timestamp) {
                    this.filePaths.add(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Get list of directory paths
     * @return list of directory paths
     */
    public List<String> getDirPaths() {
        return dirPaths;
    }

    /**
     * Get list of file paths
     * @return list of file paths
     */
    public List<String> getFilePaths() {
        return filePaths;
    }

    /**
     * Count directory contents
     * @param folderPath folder path
     * @return number of contents
     */
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