package dto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConfigManager class for storing configuration information.
 */
public class ConfigManager {

    private long lastRunTimestamp;

    private DatabaseConfig databaseConfig;

    /**
     * Constructor
     */
    public ConfigManager() {

    }

    /**
     * Load last run timestamp from file
     * @param lastRunTimestampPath path to last run timestamp file
     * @throws IOException if error occurs
     */
    public void loadLastRunTimestamp(Path lastRunTimestampPath) throws IOException {
        byte[] rawByteData = Files.readAllBytes(lastRunTimestampPath);
        String strData = new String(rawByteData, StandardCharsets.UTF_8);
        lastRunTimestamp = Long.parseLong(strData);
    }

    /**
     * Get last run timestamp
     * @return last run timestamp
     */
    public long getLastRunTimestamp() {
     return lastRunTimestamp;
    }

    /**
     * Load configuration from file
     * @param databaseConfigPath databaseConfigPath to configuration file
     * @throws IOException if error occurs
     */
    public void loadDatabaseConfig(Path databaseConfigPath) throws IOException {
        byte[] rawByteData = Files.readAllBytes(databaseConfigPath);
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
