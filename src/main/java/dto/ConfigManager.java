package dto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConfigManager class for storing configuration information.
 */
public class ConfigManager {

    private DatabaseConfig databaseConfig;

    /**
     * Constructor
     */
    public ConfigManager() {

    }

    /**
     * Load configuration from file
     * @param path path to configuration file
     * @throws IOException if error occurs
     */
    public void load(Path path) throws IOException {
        byte[] rawByteData = Files.readAllBytes(path);
        String strData = new String(rawByteData, StandardCharsets.UTF_8);
        databaseConfig = new DatabaseConfig(strData);
    }

    /**
     * Get database configuration
     * @return database configuration
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
