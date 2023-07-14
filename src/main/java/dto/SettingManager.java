package dto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

public class SettingManager {

    /// The path to the Java KeyStore file.
    private final Path settingFile = Path.of("credential.jks");

    private final String password;

    public SettingManager(String password) {
        this.password = password;
    }

    /// Creates a new Java KeyStore file.
    /// @return The path to the Java KeyStore file.
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
