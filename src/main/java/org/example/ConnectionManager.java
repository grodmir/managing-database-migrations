package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    /**
     * Создаёт и возвращает новое подключение к базе данных.
     *
     * @return Объект Connection.
     * @throws SQLException Если соединение не удалось.
     */
    public static Connection getConnection() throws SQLException {
        String url = PropertiesUtils.getProperty("db.url");
        String username = PropertiesUtils.getProperty("db.username");
        String password = PropertiesUtils.getProperty("db.password");

        // Загружаем драйвер (необязательно для современных JVM, но полезно для логирования)
        try {
            Class.forName(PropertiesUtils.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found: " + e.getMessage(), e);
        }

        return DriverManager.getConnection(url, username, password);
    }
}
