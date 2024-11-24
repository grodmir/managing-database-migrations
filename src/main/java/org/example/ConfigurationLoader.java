package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigurationLoader {
    private static ConfigurationLoader instance;
    private static String  CONFIG_FILE = "migrations.properties";
    private final Properties properties;

    public ConfigurationLoader() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigurationLoader getInstance() {
        if (instance == null) {
            synchronized (ConfigurationLoader.class) {
                if (instance == null) {
                    instance = new ConfigurationLoader();
                }
            }
        }
        return instance;
    }

    /**
     * Загружает файл конфигурации.
     */
    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Не удалось загрузить файл конфигурации '{}': {}", CONFIG_FILE, e.getMessage(), e);
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.error("Свойство '{}' не найдено в конфигурации.", key);
            throw new RuntimeException("Property '" + key + "' not found in configuration.");
        }
        log.debug("Получено свойство '{}' со значением '{}'.", key, value);
        return value;
    }
}
