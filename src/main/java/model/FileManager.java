package model;

import dao.ConfigManager;
import dao.DirMetadataDao;
import model.metadata.DirMetadata;
import model.metadata.FileMetadata;
import dao.FileMetadataDao;
import model.hasher.Hasher;
import model.searcher.ModifiedContentSearch;
import model.searcher.TotalSearch;

import java.io.IOException;
import java.util.*;

/**
 * Class for managing files.
 */
public class FileManager {

    private final ConfigManager configManager;

    private List<String> dirPaths;
    private List<String> filePaths;

    private List<FileMetadata> duplicateFiles;

    private final Hasher hasher;

    /**
     * Constructor for FileManager.
     * @param configManager ConfigManager object
     * @param hasher Hasher object
     */
    public FileManager(ConfigManager configManager, Hasher hasher) {
        this.configManager = configManager;
        this.hasher = hasher;
        try {
            configManager.loadLastRunTimestamp();
        } catch (IOException e) {
            configManager.setLastRunTimestamp(System.currentTimeMillis());
        }
    }

    /**
     * Update all files.
     * @param rootPath root path of the file
     * @param dirMetadataDao DirMetadataDao object
     * @param fileMetadataDao FileMetadataDao object
     */
    public void updateAll(
            String rootPath,
            DirMetadataDao dirMetadataDao,
            FileMetadataDao fileMetadataDao) {
        TotalSearch totalSearch = new TotalSearch(rootPath);
        this.dirPaths = totalSearch.getDirPaths();
        this.filePaths = totalSearch.getFilePaths();
        updateTotalDir(dirMetadataDao);
        updateTotalFile(fileMetadataDao);
    }

    private void updateTotalDir(DirMetadataDao dirMetadataDao) {
        for (String dirPath : dirPaths) {
            List<DirMetadata> dirList = dirMetadataDao.searchByPath(dirPath);
            if (dirList.isEmpty()) {
                dirMetadataDao.insert(DirMetadata.create(dirPath));
            }
            else {
                dirMetadataDao.updateByPath(dirPath, DirMetadata.create(dirPath));
            }
        }
    }

    private void updateTotalFile(FileMetadataDao fileMetadataDao) {
        for (String filePath : filePaths) {
            FileMetadata fileMetadata = FileMetadata.create(filePath, hasher);
            List<FileMetadata> dupPathMetadataInDB = fileMetadataDao.searchByPath(filePath);

            // If the file is in the database,
            // update the metadata.
            if (!dupPathMetadataInDB.isEmpty()) {
                fileMetadataDao.updateByPath(filePath, fileMetadata);
            }
            // If the file is not in the database,
            // insert the file into the database.
            else {
                fileMetadataDao.insert(fileMetadata);
            }
        }
    }

    /**
     * Update modified contents.
     * @param rootPath root path of the file
     * @param dirMetadataDao DirMetadataDao object
     * @param fileMetadataDao FileMetadataDao object
     */
    public void updateModifiedContent(
            String rootPath,
            DirMetadataDao dirMetadataDao,
            FileMetadataDao fileMetadataDao) {

        // Search modified contents
        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(
                rootPath,
                configManager.getLastRunTimestamp()
        );
        this.dirPaths = modifiedContentSearch.getDirPaths();
        this.filePaths = modifiedContentSearch.getFilePaths();
        updateModifiedDir(dirMetadataDao);
        this.duplicateFiles = updateModifiedFile(fileMetadataDao);
    }

    private void updateModifiedDir(DirMetadataDao dirMetadataDao) {
        if (!dirPaths.isEmpty()) {
            for (String modifiedDirPath : dirPaths) {
                List<DirMetadata> dirList = dirMetadataDao.searchByPath(modifiedDirPath);
                if (dirList.isEmpty()) {
                    dirMetadataDao.insert(DirMetadata.create(modifiedDirPath));
                }
                else {
                    long currentContentCount = DirMetadata.getActualDirContentCount(modifiedDirPath);
                    long currentLastModified = DirMetadata.getActualDirModifiedTime(modifiedDirPath);
                    for (DirMetadata dirMetadata : dirList) {
                        if (dirMetadata.contentCount() != currentContentCount) {
                            dirMetadataDao.updateContentCount(dirMetadata.path(), currentContentCount);
                        }
                        if (dirMetadata.lastModified() != currentLastModified) {
                            dirMetadataDao.updateLastModified(dirMetadata.path(), currentLastModified);
                        }
                    }
                }
            }
        }
    }


    private List<FileMetadata> updateModifiedFile(FileMetadataDao fileMetadataDao) {
        Set<FileMetadata> result = new HashSet<>();
        if (!filePaths.isEmpty()) {
            for (String modifiedFilePath : filePaths) {
                FileMetadata modifiedFileMetadata = FileMetadata.create(modifiedFilePath, hasher);
                List<FileMetadata> dupPathMetadataInDB = fileMetadataDao.searchByPath(modifiedFilePath);

                // If the file is in the database,
                // check if the file has been modified or not.
                if (!dupPathMetadataInDB.isEmpty()) {
                    for (FileMetadata metadata : dupPathMetadataInDB) {
                        if (!metadata.hash().equals(modifiedFileMetadata.hash())) {
                            fileMetadataDao.updateByPath(modifiedFilePath, modifiedFileMetadata);
                        }
                    }
                }
                // If the file is not in the database,
                // search the database by hash.
                else {
                    List<FileMetadata> sameHashFile = fileMetadataDao.searchByHash(modifiedFileMetadata.hash());
                    // If the hash is not in the database,
                    // file is a new file.
                    // Insert the file into the database.
                    if (sameHashFile.isEmpty()) {
                        fileMetadataDao.insert(modifiedFileMetadata);
                    }
                    // If the hash is in the database,
                    // the file is a duplicate file.
                    // Check equality between paths
                    // and if they are not equal,
                    // add it as a duplicate file list.
                    else {
                        for (FileMetadata metadata : sameHashFile) {
                            if (!metadata.path().equals(modifiedFilePath)) {
                                result.add(metadata);
                                result.add(modifiedFileMetadata);
                            }
                        }
                    }
                }
            }
        }
        return result.isEmpty() ? new ArrayList<>() : new ArrayList<>(result);
    }

    /**
     * Get duplicate files.
     * @return duplicate files
     */
    public List<FileMetadata> getDuplicateFiles() {
        return duplicateFiles;
    }

    /**
     * Delete duplicate files.
     */
    public void deleteDuplicateFiles() {
        if (duplicateFiles.isEmpty()) {
            return;
        }
        // First file is the original file. Do not delete it.
        for (int i = 1; i < duplicateFiles.size(); i++) {
            FileDelete.delete(duplicateFiles.get(i).path());
        }
    }
}

