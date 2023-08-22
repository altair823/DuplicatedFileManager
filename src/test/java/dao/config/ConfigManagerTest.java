package dao.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.ConfigManager;
import model.config.DatabaseConfig;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigManagerTest {

    @Test
    void loadDatabaseConfigTest() throws IOException {
        DatabaseConfig configs = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "postgres"
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(configs);

        // Save json to file
        BufferedWriter writer = new BufferedWriter(new FileWriter("configs.json"));
        writer.write(json);
        writer.close();

        // Load json from file
        ConfigManager configManager = new ConfigManager();
        String configFile = "configs.json";
        configManager.loadDatabaseConfig(configFile);
        assertEquals(configs, configManager.getDatabaseConfig());

        // Delete json file
        Files.deleteIfExists(Path.of(configFile));
    }

    @Test
    void loadLastRunTimestampTest(){
        ConfigManager configManager = new ConfigManager();
        Path lastRunTimestampPath = Path.of("lastRunTimestamp.txt");
        try {
            Files.writeString(lastRunTimestampPath, "123456789");
            configManager.loadLastRunTimestamp();
            assertEquals(123456789, configManager.getLastRunTimestamp());
            Files.deleteIfExists(lastRunTimestampPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}