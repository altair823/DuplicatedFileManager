package dto;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettingManagerTest {

    final String password = "password";

    @Test
    public void createSettingFileTest() throws Exception {
        SettingManager settingManager = new SettingManager(password);
        Path settingFile = settingManager.createSettingFile();

        // assert Java KeyStore file exists
        assertTrue(Files.exists(settingFile));

        // delete Java KeyStore file
        Files.deleteIfExists(settingFile);
    }
}
