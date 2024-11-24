package org.example.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс для загрузки и управления конфигурационными свойствами.
 * Реализует паттерн Singleton для обеспечения одного экземпляра.
 */
@Slf4j
public class ConfigurationLoader {
    private static ConfigurationLoader instance;
    private static String  CONFIG_FILE = "migrations.properties";
    private final Properties properties;

    public ConfigurationLoader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Возвращает единственный экземпляр конфигурационного загрузчика.
     * Если экземпляр ещё не создан, он будет инициализирован.
     *
     * @return Экземпляр класса {@link ConfigurationLoader}.
     */
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

    private void loadProperties() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Не удалось загрузить файл конфигурации '{}': {}", CONFIG_FILE, e.getMessage(), e);
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
    }

    /**
     * Получает значение свойства из конфигурационного файла.
     * Если свойство не найдено, генерируется исключение {@link RuntimeException}.
     *
     * @param key Ключ свойства.
     * @return Значение свойства.
     * @throws RuntimeException если свойство не найдено.
     */
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
