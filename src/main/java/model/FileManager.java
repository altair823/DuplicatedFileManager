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
import java.util.LinkedList;
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

    public List<FileMetadata> updateModifiedContentPaths(
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
        List<FileMetadata> result = new LinkedList<>();
        if (!modifiedFilePaths.isEmpty()) {
            for (String modifiedFilePath : modifiedFilePaths) {
                FileMetadata modifiedFileMetadata;
                try {
                    modifiedFileMetadata = new FileMetadata(
                            modifiedFilePath,
                            FileMetadata.getActualFileModifiedTime(modifiedFilePath),
                            FileMetadata.getActualFileSize(modifiedFilePath),
                            hasher.makeHash(new FileInputStream(modifiedFilePath))
                    );
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                List<FileMetadata> metadataInDB;
                metadataInDB = fileMetadataDto.searchByPath(modifiedFilePath);
                // If the file is in the database,
                // check if the file has been modified or not.
                if (!metadataInDB.isEmpty()) {
                    for (FileMetadata metadata : metadataInDB) {
                        if (!metadata.hash().equals(modifiedFileMetadata.hash())) {
                            fileMetadataDto.updateLastModified(metadata.path(), modifiedFileMetadata.lastModified());
                            fileMetadataDto.updateSize(metadata.path(), modifiedFileMetadata.size());
                            fileMetadataDto.updateHash(metadata.path(), modifiedFileMetadata.hash());
                        }
                    }
                }
                // If the file is not in the database,
                // calculate hash and search the database by hash.
                else {
                    List<FileMetadata> metadataList = fileMetadataDto.searchByHash(modifiedFileMetadata.hash());
                    // If the hash is not in the database,
                    // file is a new file.
                    // Insert the file into the database.
                    if (metadataList.isEmpty()) {
                        fileMetadataDto.insert(
                                new FileMetadata(
                                        modifiedFilePath,
                                        modifiedFileMetadata.lastModified(),
                                        modifiedFileMetadata.size(),
                                        modifiedFileMetadata.hash()
                                )
                        );
                    }
                    // If the hash is in the database,
                    // the file is a duplicate file.
                    // Check equality between paths
                    // and if they are not equal,
                    // add it as a duplicate file list.
                    else {
                        for (FileMetadata metadata : metadataList) {
                            if (!metadata.path().equals(modifiedFilePath)) {
                                result.add(modifiedFileMetadata);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}

