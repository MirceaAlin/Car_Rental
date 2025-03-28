package Settings;

import java.io.InputStream;
import java.util.Properties;

public class Settings {
    private static Settings instance;
    private Properties properties;

    private Settings() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("settings.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find settings.properties");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading settings: " + e.getMessage());
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String getRepoType() {
        return properties.getProperty("Repository");
    }

    public String getMasinaDbUrl() {
        return properties.getProperty("masina.db.url");
    }

    public String getInchiriereDbUrl() {
        return properties.getProperty("inchiriere.db.url");
    }
}
