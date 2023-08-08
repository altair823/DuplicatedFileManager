package dto.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.config.DatabaseConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    @Test
    void serializeTest() {
        DatabaseConfig databaseConfig = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "postgres"
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = databaseConfig.serialize();
        DatabaseConfig databaseConfig2 = gson.fromJson(json, DatabaseConfig.class);
        assertEquals(databaseConfig, databaseConfig2);
    }

    @Test
    void deserializeTest() {
        DatabaseConfig databaseConfig = new DatabaseConfig(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "postgres"
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(databaseConfig);
        DatabaseConfig databaseConfig2 = new DatabaseConfig(json);
        assertEquals(databaseConfig, databaseConfig2);
    }
}