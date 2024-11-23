package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MigrationManager {

    private final MigrationFileReader migrationFileReader;

    public MigrationManager(MigrationFileReader migrationFileReader) {
        this.migrationFileReader = migrationFileReader;
    }

    /**
     * Возвращает список миграций, которые ещё не были применены.
     *
     * @return список миграций, которые ещё не были применены.
     */
    public List<MigrationFile> getPendingMigrations() throws IOException {
        log.info("Получаем список всех не выполненных миграций...");
        List<MigrationFile> allMigrations = migrationFileReader.readMigrationFiles();
        Set<String> appliedMigrations = getAppliedMigrations();

        if (allMigrations.isEmpty()) {
            log.error("Не найдены файлы миграций в папке. Проверьте вашу конфигурацию.");
            throw new IllegalStateException("No migration files found in the folder. Check your configuration.");
        }

        List<MigrationFile> pendingMigrations = allMigrations.stream()
                .filter(migration -> !appliedMigrations.contains(migration.getFileName()))
                .sorted(Comparator.comparing(migration -> extractTimestamp(migration.getFileName())))
                .collect(Collectors.toList());

        log.info("Количество ожидающих миграций: {}", pendingMigrations.size());
        return pendingMigrations;
    }

    private Set<String> getAppliedMigrations() {
        log.info("Пытаемся получить выполенные миграции...");

        String query = "SELECT file_name FROM applied_migrations";
        Set<String> appliedMigrations = new HashSet<>();

        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                appliedMigrations.add(resultSet.getString("file_name"));
            }

            log.debug("Количество выполненных миграций: {}", appliedMigrations.size());

        } catch (SQLException e) {
            if (e.getMessage().contains("applied_migrations")) {
                log.error("Таблица 'applied_migrations' не найдена. Убедитесь, что схема базы данных инициализирована.", e);
                throw new IllegalStateException("Table 'applied_migrations' not found.", e);
            } else {
                log.error("Ошибка при запросе к базе данных: {}", e.getMessage(), e);
                throw new IllegalStateException("Error while querying the database.", e);
            }
        }

        return appliedMigrations;
    }

    private long extractTimestamp(String fileName) {
        // Предполагаем, что имя файла начинается с временной метки формата YYYYMMDDHHMMSS
        try {
            String timestampPart = fileName.split("_")[0];
            return Long.parseLong(timestampPart);
        } catch (Exception e) {
            log.error("Неверный формат имени файла миграции: {}", fileName, e);
            throw new IllegalArgumentException("Invalid migration file name format: " + fileName, e);
        }
    }
}
