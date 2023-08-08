package dto.config;

import com.google.gson.GsonBuilder;
import java.util.Objects;

/**
 * DTO for storing database connection information.
 */
public class DatabaseConfig extends Config {
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    /**
     * Constructor for DatabaseConfig.
     * @param databaseUrl URL of the database
     * @param databaseUser username of the database
     * @param databasePassword password of the database
     */
    public DatabaseConfig(String databaseUrl, String databaseUser, String databasePassword) {
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    /**
     * Constructor for DatabaseConfig.
     * Example values are given.
     */
    public DatabaseConfig() {
        this.databaseUrl = "URL of the database";
        this.databaseUser = "username of the database";
        this.databasePassword = "password of the database";
    }

    /**
     * Constructor for DatabaseConfig.
     * @param json JSON string
     */
    public DatabaseConfig(String json) {
        deserialize(json);
    }

    /**
     * Getter for databaseUrl.
     * @return databaseUrl
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Getter for databaseUser.
     * @return databaseUser
     */
    public String getDatabaseUser() {
        return databaseUser;
    }

    /**
     * Getter for databasePassword.
     * @return databasePassword
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    @Override
    public String serialize() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @Override
    public void deserialize(String json) {
        DatabaseConfig databaseConfig = new GsonBuilder().create().fromJson(json, DatabaseConfig.class);
        this.databaseUrl = databaseConfig.getDatabaseUrl();
        this.databaseUser = databaseConfig.getDatabaseUser();
        this.databasePassword = databaseConfig.getDatabasePassword();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatabaseConfig that)) return false;
        return Objects.equals(getDatabaseUrl(), that.getDatabaseUrl())
                && Objects.equals(getDatabaseUser(), that.getDatabaseUser())
                && Objects.equals(getDatabasePassword(), that.getDatabasePassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDatabaseUrl(), getDatabaseUser(), getDatabasePassword());
    }
}
