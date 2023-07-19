package model;

import dto.ConfigManager;
import hasher.Hasher;

import java.util.List;

public class FileManager {

    private ConfigManager configManager;

    private List<String> modifiedContentPaths;

    private Hasher hasher;

    public FileManager() {

    }
}
