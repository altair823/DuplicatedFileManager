package model;

import dto.ConfigManager;
import dto.metadata.dir.DirMetadata;
import dto.metadata.dir.DirMetadataDto;
import dto.metadata.file.FileMetadata;
import dto.metadata.file.FileMetadataDto;
import hasher.Hasher;
import searcher.ModifiedContentSearch;

import java.io.IOException;
import java.util.*;

public class FileManager {

    private final ConfigManager configManager;

    private List<String> modifiedDirPaths;
    private List<String> modifiedFilePaths;

    private final Hasher hasher;

    public FileManager(ConfigManager configManager, Hasher hasher) {
        this.configManager = configManager;
        this.hasher = hasher;
        try {
            configManager.loadLastRunTimestamp();
        } catch (IOException e) {
            configManager.setLastRunTimestamp(System.currentTimeMillis());
        }
    }

    public void updateAll(
            String rootPath,
            DirMetadataDto dirMetadataDto,
            FileMetadataDto fileMetadataDto) {

    }

    public List<FileMetadata> updateModifiedContentPaths(
            String rootPath,
            DirMetadataDto dirMetadataDto,
            FileMetadataDto fileMetadataDto) {

        // Search modified contents
        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(
                rootPath,
                configManager.getLastRunTimestamp()
        );
        this.modifiedDirPaths = modifiedContentSearch.getDirPaths();
        this.modifiedFilePaths = modifiedContentSearch.getFilePaths();
        // Update modified directories
        updateModifiedDir(dirMetadataDto);
        // Update modified files
        return updateModifiedFile(fileMetadataDto);
    }

    private void updateModifiedDir(DirMetadataDto dirMetadataDto) {
        if (!modifiedDirPaths.isEmpty()) {
            for (String modifiedDirPath : modifiedDirPaths) {
                List<DirMetadata> dirList = dirMetadataDto.searchByPath(modifiedDirPath);
                if (dirList.isEmpty()) {
                    dirMetadataDto.insert(
                            new DirMetadata(
                                    modifiedDirPath,
                                    DirMetadata.getActualDirModifiedTime(modifiedDirPath),
                                    DirMetadata.getActualDirContentCount(modifiedDirPath)
                            )
                    );
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
        if (!modifiedFilePaths.isEmpty()) {
            for (String modifiedFilePath : modifiedFilePaths) {
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
                    List<FileMetadata> metadataList = fileMetadataDto.searchByHash(modifiedFileMetadata.hash());
                    // If the hash is not in the database,
                    // file is a new file.
                    // Insert the file into the database.
                    if (metadataList.isEmpty()) {
                        fileMetadataDto.insert(modifiedFileMetadata);
                    }
                    // If the hash is in the database,
                    // the file is a duplicate file.
                    // Check equality between paths
                    // and if they are not equal,
                    // add it as a duplicate file list.
                    else {
                        for (FileMetadata metadata : metadataList) {
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
}

