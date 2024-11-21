package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_SQL);
            System.out.println("Migration table checked/created successfully.");
        } catch (SQLException e) {
            throw new IllegalStateException("Error while ensuring migration table exists: " + e.getMessage(), e);
        }
    }
}
