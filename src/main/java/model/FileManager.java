package model;

import dto.ConfigManager;
import dto.metadata.dir.DirMetadata;
import dto.metadata.dir.DirMetadataDto;
import dto.metadata.file.FileMetadata;
import dto.metadata.file.FileMetadataDto;
import hasher.Hasher;
import searcher.ModifiedContentSearch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FileManager {

    private final ConfigManager configManager;

    private List<String> modifiedDirPaths;
    private List<String> modifiedFilePaths;

    private final Hasher hasher;

    public FileManager(ConfigManager configManager, Hasher hasher) {
        this.configManager = configManager;
        this.hasher = hasher;
    }

    public void updateModifiedContentPaths(
            String rootPath,
            DirMetadataDto dirMetadataDto,
            FileMetadataDto fileMetadataDto) {
        try {
            configManager.loadLastRunTimestamp();
        } catch (IOException e) {
            configManager.setLastRunTimestamp(System.currentTimeMillis());
        }
        long timestamp = configManager.getLastRunTimestamp();
        // Search modified contents
        ModifiedContentSearch modifiedContentSearch = new ModifiedContentSearch(rootPath, timestamp);
        this.modifiedDirPaths = modifiedContentSearch.getDirPaths();
        this.modifiedFilePaths = modifiedContentSearch.getFilePaths();
        // Update modified directories
        updateModifiedDir(dirMetadataDto);
        // Update modified files
        updateModifiedFile(fileMetadataDto);
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


    private void updateModifiedFile(FileMetadataDto fileMetadataDto) {
        if (!modifiedFilePaths.isEmpty()) {
            for (String modifiedFilePath : modifiedFilePaths) {
                List<FileMetadata> metadataInDB;
                metadataInDB = fileMetadataDto.searchByPath(modifiedFilePath);
                // If the file is in the database,
                // check if the file has been modified or not.
                if (!metadataInDB.isEmpty()) {
                    String newHash;
                    long newLastModified;
                    long newSize;
                    try {
                        newHash = hasher.makeHash(new FileInputStream(modifiedFilePath));
                        newLastModified = FileMetadata.getActualFileModifiedTime(modifiedFilePath);
                        newSize = FileMetadata.getActualFileSize(modifiedFilePath);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    for (FileMetadata metadata : metadataInDB) {
                        if (!metadata.hash().equals(newHash)) {
                            fileMetadataDto.updateLastModified(metadata.path(), newLastModified);
                            fileMetadataDto.updateSize(metadata.path(), newSize);
                            fileMetadataDto.updateHash(metadata.path(), newHash);
                        }
                    }
                }
            }
        }
    }
}

