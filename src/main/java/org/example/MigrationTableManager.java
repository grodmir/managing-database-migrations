package org.example;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class MigrationTableManager {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS applied_migrations (
                id SERIAL PRIMARY KEY,
                file_name VARCHAR(255) UNIQUE NOT NULL,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
    """;

    /**
     * Проверяет и создаёт таблицу для хранения применённых миграций.
     */
    public static void ensureMigrationTableExists() {
        log.info("Проверка и создание таблицы для хранения применённых миграций...");

        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
            log.info("Таблица миграций успешно проверена или создана.");
        } catch (SQLException e) {
            log.error("Ошибка при проверке или создании таблицы миграций: {}", e.getMessage(), e);
            throw new IllegalStateException("Error while ensuring migration table exists: " + e.getMessage(), e);
        }
    }
}
