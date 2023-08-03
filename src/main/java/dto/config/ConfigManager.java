package dto.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConfigManager class for storing configuration information.
 */
public class ConfigManager {

    public static final String TIMESTAMP_FILE_NAME = "lastRunTimestamp.txt";

    public static final String DB_CONFIG_FILE_NAME = "dbConfig.json";

    private long lastRunTimestamp;

    private DatabaseConfig databaseConfig;

    /**
     * Constructor
     */
    public ConfigManager() {

    }

    /**
     * Load last run timestamp from file
     * @throws IOException if error occurs
     */
    public void loadLastRunTimestamp() throws IOException {
        byte[] rawByteData = Files.readAllBytes(Path.of(TIMESTAMP_FILE_NAME));
        String strData = new String(rawByteData, StandardCharsets.UTF_8);
        lastRunTimestamp = Long.parseLong(strData);
    }

    /**
     * Set last run timestamp
     * @param timestamp timestamp to set
     */
    public void setLastRunTimestamp(long timestamp) {
        lastRunTimestamp = timestamp;
    }

    /**
     * Create new last run timestamp file
     * @param timestamp timestamp to write
     * @throws IOException if error occurs
     */
    public static void createNewLastRunTimestamp(long timestamp) throws IOException {
        Files.writeString(Path.of(TIMESTAMP_FILE_NAME), String.valueOf(timestamp));
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
