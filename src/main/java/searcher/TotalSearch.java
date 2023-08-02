package searcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Search all files and directories recursively
 */
public class TotalSearch {

    private final List<String> dirPaths;

    private final List<String> filePaths;

    /**
     * Constructor
     * Files and directories modified after the timestamp are searched recursively when this object is created.
     * @param rootPath root path to search
     */
    public TotalSearch(String rootPath) {
        this.dirPaths = new LinkedList<>();
        this.filePaths = new LinkedList<>();
        File root = new File(rootPath);
        searchAll(root);
    }

    private void searchAll(File root) {
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.dirPaths.add(file.getAbsolutePath());
                searchAll(file);
            } else {
                this.filePaths.add(file.getAbsolutePath());
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
}
