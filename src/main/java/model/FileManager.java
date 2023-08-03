package model;

import dto.ConfigManager;
import dto.metadata.dir.DirMetadata;
import dto.metadata.dir.DirMetadataDto;
import dto.metadata.file.FileMetadata;
import dto.metadata.file.FileMetadataDto;
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
     * @param dirMetadataDto DirMetadataDto object
     * @param fileMetadataDto FileMetadataDto object
     */
    public void updateAll(
            String rootPath,
            DirMetadataDto dirMetadataDto,
            FileMetadataDto fileMetadataDto) {
        TotalSearch totalSearch = new TotalSearch(rootPath);
        this.dirPaths = totalSearch.getDirPaths();
        this.filePaths = totalSearch.getFilePaths();
        updateTotalDir(dirMetadataDto);
        updateTotalFile(fileMetadataDto);
    }

    private void updateTotalDir(DirMetadataDto dirMetadataDto) {
        for (String dirPath : dirPaths) {
            List<DirMetadata> dirList = dirMetadataDto.searchByPath(dirPath);
            if (dirList.isEmpty()) {
                dirMetadataDto.insert(DirMetadata.create(dirPath));
            }
            else {
                dirMetadataDto.updateByPath(dirPath, DirMetadata.create(dirPath));
            }
        }
    }

    private void updateTotalFile(FileMetadataDto fileMetadataDto) {
        for (String filePath : filePaths) {
            FileMetadata fileMetadata = FileMetadata.create(filePath, hasher);
            List<FileMetadata> dupPathMetadataInDB = fileMetadataDto.searchByPath(filePath);

            // If the file is in the database,
            // update the metadata.
            if (!dupPathMetadataInDB.isEmpty()) {
                fileMetadataDto.updateByPath(filePath, fileMetadata);
            }
            // If the file is not in the database,
            // insert the file into the database.
            else {
                fileMetadataDto.insert(fileMetadata);
            }
        }
    }

    /**
     * Update modified contents.
     * @param rootPath root path of the file
     * @param dirMetadataDto DirMetadataDto object
     * @param fileMetadataDto FileMetadataDto object
     */
    public void updateModifiedContent(
            String rootPath,
            DirMetadataDto dirMetadataDto,
            FileMetadataDto fileMetadataDto) {

        // Search modified contents
        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(
                rootPath,
                configManager.getLastRunTimestamp()
        );
        this.dirPaths = modifiedContentSearch.getDirPaths();
        this.filePaths = modifiedContentSearch.getFilePaths();
        updateModifiedDir(dirMetadataDto);
        this.duplicateFiles = updateModifiedFile(fileMetadataDto);
    }

    private void updateModifiedDir(DirMetadataDto dirMetadataDto) {
        if (!dirPaths.isEmpty()) {
            for (String modifiedDirPath : dirPaths) {
                List<DirMetadata> dirList = dirMetadataDto.searchByPath(modifiedDirPath);
                if (dirList.isEmpty()) {
                    dirMetadataDto.insert(DirMetadata.create(modifiedDirPath));
                }
                else {
                    long currentContentCount = DirMetadata.getActualDirContentCount(modifiedDirPath);
                    long currentLastModified = DirMetadata.getActualDirModifiedTime(modifiedDirPath);
                    for (DirMetadata dirMetadata : dirList) {
                        if (dirMetadata.contentCount() != currentContentCount) {
                            dirMetadataDto.updateContentCount(dirMetadata.path(), currentContentCount);
                        }
                        if (dirMetadata.lastModified() != currentLastModified) {
                            dirMetadataDto.updateLastModified(dirMetadata.path(), currentLastModified);
                        }
                    }
                }
            }
        }
    }


    private List<FileMetadata> updateModifiedFile(FileMetadataDto fileMetadataDto) {
        Set<FileMetadata> result = new HashSet<>();
        if (!filePaths.isEmpty()) {
            for (String modifiedFilePath : filePaths) {
                FileMetadata modifiedFileMetadata = FileMetadata.create(modifiedFilePath, hasher);
                List<FileMetadata> dupPathMetadataInDB = fileMetadataDto.searchByPath(modifiedFilePath);

                // If the file is in the database,
                // check if the file has been modified or not.
                if (!dupPathMetadataInDB.isEmpty()) {
                    for (FileMetadata metadata : dupPathMetadataInDB) {
                        if (!metadata.hash().equals(modifiedFileMetadata.hash())) {
                            fileMetadataDto.updateByPath(modifiedFilePath, modifiedFileMetadata);
                        }
                    }
                }
                // If the file is not in the database,
                // search the database by hash.
                else {
                    List<FileMetadata> sameHashFile = fileMetadataDto.searchByHash(modifiedFileMetadata.hash());
                    // If the hash is not in the database,
                    // file is a new file.
                    // Insert the file into the database.
                    if (sameHashFile.isEmpty()) {
                        fileMetadataDto.insert(modifiedFileMetadata);
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
}

