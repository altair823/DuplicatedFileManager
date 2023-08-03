package dto.config;

/**
 * Abstract class for storing configuration information.
 */
public abstract class Config {
    /**
     * Serialize this object to JSON.
     * @return JSON string
     */
    public abstract String serialize();

    /**
     * Deserialize this object from JSON.
     * @param json JSON string
     */
    public abstract void deserialize(String json);
}
