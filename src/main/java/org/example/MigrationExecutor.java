package org.example;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class MigrationExecutor {

    private static final String INSERT_MIGRATION_SQL = "INSERT INTO applied_migrations (file_name, applied_at) VALUES (?, CURRENT_TIMESTAMP)";

    /**
     * Выполняет миграции в одной транзакции.
     *
     * @param migrations список миграций, которые нужно выполнить
     */
    public void executeMigrations(List<MigrationFile> migrations) {
        if (migrations.isEmpty()) {
            log.warn("Нет новых миграций для применения.");
            return;
        }

        try(Connection connection = ConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            log.info("Начинаем выполнение миграций...");

            try {
                for (MigrationFile migration : migrations) {
                    applyMigration(connection, migration);
                }

                for (MigrationFile migration : migrations) {
                    logMigrationAsExecuted(connection, migration.getFileName());
                }

                connection.commit();
                log.info("Все миграции успешно применены.");

            } catch (SQLException e) {
                connection.rollback();
                log.error("Ошибка при выполнении миграции. Транзакция отменена: {}", e.getMessage(), e);
                throw new SQLException("An error occurred while executing migration. The transaction was cancelled.", e);
            }

        } catch (SQLException e) {
            log.error("Не удалось выполнить миграции: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to complete migration: " + e.getMessage(), e);
        }
    }

    private void applyMigration(Connection connection, MigrationFile migration) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(migration.getContent())) {
            statement.executeUpdate();
            log.info("Применена миграция: {}", migration.getFileName());
        } catch (SQLException e) {
            log.error("Ошибка при применении миграции {}: {}", migration.getFileName(), e.getMessage(), e);
            throw e; // Пробрасываем исключение дальше
        }
    }

    private void logMigrationAsExecuted(Connection connection, String fileName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_MIGRATION_SQL)) {
            statement.setString(1, fileName);
            statement.executeUpdate();
            log.info("Миграция {} отмечена как выполненная.", fileName);
        } catch (SQLException e) {
            log.error("Ошибка при записи миграции {} в таблицу выполненных: {}", fileName, e.getMessage(), e);
            throw e; // Пробрасываем исключение дальше
        }
    }
}
