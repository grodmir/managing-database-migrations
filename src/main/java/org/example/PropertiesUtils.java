package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesUtils {
    private static PropertiesUtils instance;
    private Properties properties;

    private PropertiesUtils() {
        properties = new Properties();
        loadProperties();
    }

    public static PropertiesUtils getInstance() {
        if (instance == null) {
            synchronized (PropertiesUtils.class) {
                if (instance == null) {
                    instance = new PropertiesUtils();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                log.error("Файл конфигурации «application.properties» не найден в classpath.");
                throw new RuntimeException("Configuration file 'application.properties' not found in the classpath.");
            }
            properties.load(input);
            log.info("Успешно загружено 'application.properties'.");
        } catch (IOException ex) {
            log.error("Ошибка загрузки 'application.properties': {}", ex.getMessage(), ex);
            throw new RuntimeException("Error loading 'application.properties': " + ex.getMessage(), ex);
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
