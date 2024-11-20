package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Configuration file 'application.properties' not found in the classpath.");
            }
            properties.load(input);
        } catch (IOException  ex) {
            throw new RuntimeException("Error loading 'application.properties': " + ex.getMessage(), ex);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in configuration.");
        }
        return value;
    }
}
