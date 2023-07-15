package dto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

/**
 * Setting manager
 */
public class SettingManager {

    /**
     * Java KeyStore file
     */
    private final Path settingFile = Path.of("credential.jks");

    /**
     * Password for Java KeyStore file
     */
    private final String password;

    /**
     * Constructor
     * @param password password
     */
    public SettingManager(String password) {
        this.password = password;
    }

    /**
     * Create Java KeyStore file when it does not exist
     * @return Java KeyStore file
     * @throws Exception if error occurs
     */
    public Path createSettingFile() throws Exception {
        if (settingFile.toFile().exists()) {
            throw new IllegalStateException("The setting file already exists.");
        }
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, password.toCharArray());
        keyStore.store(
            Files.newOutputStream(settingFile),
            password.toCharArray()
        );
        return settingFile;
    }


}
