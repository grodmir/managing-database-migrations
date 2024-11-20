package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MigrationExecutor {

    private static final String INSERT_MIGRATION_SQL = "INSERT INTO applied_migrations (file_name, applied_at) VALUES (?, CURRENT_TIMESTAMP)";

    /**
     * Выполняет миграции в одной транзакции.
     *
     * @param migrations список миграций, которые нужно выполнить
     */
    public void executeMigrations(List<MigrationFile> migrations) {
        if (migrations.isEmpty()) {
            System.out.println("No new migrations to apply.");
            return;
        }

        try(Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                for (MigrationFile migration : migrations) {
                    applyMigration(connection, migration);
                }

                for (MigrationFile migration : migrations) {
                    logMigrationAsExecuted(connection, migration.getFileName());
                }

                connection.commit();
                System.out.println("All migrations applied successfully.");

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error during migration execution. Transaction rolled back.", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute migrations: " + e.getMessage(), e);
        }
    }

    /**
     * Применяет одну миграцию.
     *
     * @param connection соединение с базой данных
     * @param migration миграция, которую нужно выполнить
     * @throws SQLException если выполнение миграции не удалось
     */
    private void applyMigration(Connection connection, MigrationFile migration) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(migration.getContent())) {
            statement.executeUpdate();
            System.out.println("Migration applied: " + migration.getFileName());
        }
    }

    /**
     * Отмечает выполнение миграции в таблице выполненных миграций.
     *
     * @param connection соединение с базой данных
     * @param fileName   имя файла миграции
     * @throws SQLException если запись не удалась
     */
    private void logMigrationAsExecuted(Connection connection, String fileName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MIGRATION_SQL)) {
            statement.setString(1, fileName);
            statement.executeUpdate();
        }
    }
}
