package dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigManagerTest {

    @Test
    void loadTest() throws IOException {
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
        Path configFile = Path.of("configs.json");
        configManager.load(configFile);
        assertEquals(configs, configManager.getDatabaseConfig());

        // Delete json file
        Files.deleteIfExists(configFile);
    }
}