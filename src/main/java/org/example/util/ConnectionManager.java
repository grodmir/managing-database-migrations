package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConfigurationLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для управления подключениями к базе данных.
 */
@Slf4j
public class ConnectionManager {

    /**
     * Создаёт и возвращает новое подключение к базе данных.
     * Для получения параметров подключения используется {@link ConfigurationLoader}.
     *
     * @return Объект {@link Connection} для подключения к базе данных.
     * @throws SQLException если возникает ошибка при установлении подключения.
     */
    public static Connection getConnection() throws SQLException {
        log.info("Начинаем попытку установить соединение с базой данных...");

        String url = ConfigurationLoader.getInstance().getProperty("db.url");
        String username = ConfigurationLoader.getInstance().getProperty("db.username");
        String password = ConfigurationLoader.getInstance().getProperty("db.password");
        log.debug("Данные для подключения: url:{}, username:{}", url, username);

        try {
            Class.forName(ConfigurationLoader.getInstance().getProperty("db.driver"));
            log.info("JDBC драйвер загружен: {}", ConfigurationLoader.getInstance().getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            log.error("JDBC Driver не найден: {}", e.getMessage(), e);
            throw new RuntimeException("JDBC Driver not found: " + e.getMessage(), e);
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            log.info("Соединение с базой данных успешно установлено.");
            return connection;
        } catch (SQLException e) {
            log.error("Ошибка при попытке установить соединение с базой данных: {}", e.getMessage(), e);
            throw new SQLException("Failed to establish connection to database: " + e.getMessage(), e);
        }
    }
}
