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
    public static void saveLastRunTimestamp(long timestamp) throws IOException {
        Files.writeString(Path.of(TIMESTAMP_FILE_NAME), String.valueOf(timestamp));
    }

    public static long createCurrentTimestamp() {
        return System.currentTimeMillis();
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
    public void loadDatabaseConfig(String databaseConfigPath) throws IOException {
        byte[] rawByteData = Files.readAllBytes(Path.of(databaseConfigPath));
        String strData = new String(rawByteData, StandardCharsets.UTF_8);
        databaseConfig = new DatabaseConfig(strData);
    }

    public void saveDatabaseConfig(String databaseConfigPath) throws IOException {
        Files.writeString(Path.of(databaseConfigPath), databaseConfig.serialize());
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Get database configuration
     * @return database configuration
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

}
