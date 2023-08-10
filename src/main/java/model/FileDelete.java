package model;

import java.io.File;

public class FileDelete {
    public static void delete(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("Failed to delete file");
            }
        }
    }

}
