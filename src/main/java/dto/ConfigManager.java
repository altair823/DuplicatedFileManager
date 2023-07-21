package dto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ConfigManager class for storing configuration information.
 */
public class ConfigManager {

    public static final String TIMESTAMP_FILE_NAME = "lastRunTimestamp.txt";
    private static final String DATABASE_CONFIG_FILE_NAME = "databaseConfig.txt";

    private long lastRunTimestamp;

    private DatabaseConfig databaseConfig;

    /**
     * Constructor
     */
    public ConfigManager() {

    }

    public void load(String url, String username, String password) throws IOException {
        Path timestampFilePath = Path.of(TIMESTAMP_FILE_NAME);
        if (!Files.exists(timestampFilePath)) {
            createNewLastRunTimestamp();
        }
        loadLastRunTimestamp();

        Path databaseConfigPath = Path.of(DATABASE_CONFIG_FILE_NAME);
        if (!Files.exists(databaseConfigPath)) {
            createNewDatabaseConfig(databaseConfigPath, url, username, password);
        }
        loadDatabaseConfig(databaseConfigPath);
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
     * @throws IOException if error occurs
     */
    public static void createNewLastRunTimestamp() throws IOException {
        long currentTimestamp = System.currentTimeMillis();
        Files.writeString(Path.of(TIMESTAMP_FILE_NAME), String.valueOf(currentTimestamp));
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

    public void createNewDatabaseConfig(Path databaseConfigPath, String url, String username, String password) {
        databaseConfig = new DatabaseConfig(url, username, password);
        try {
            Files.writeString(databaseConfigPath, databaseConfig.serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get database configuration
     * @return database configuration
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
