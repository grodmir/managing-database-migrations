package org.example;

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
        List<MigrationFile> allMigrations = migrationFileReader.readMigrationFiles();
        Set<String> appliedMigrations = getAppliedMigrations();

        if (allMigrations.isEmpty()) {
            throw new IllegalStateException("No migration files found in the folder. Check your configuration.");
        }

        return allMigrations.stream()
                .filter(migration -> !appliedMigrations.contains(migration.getFileName()))
                .sorted(Comparator.comparing(migration -> extractTimestamp(migration.getFileName())))
                .collect(Collectors.toList());
    }

    /**
     * Загружает список выполненных миграций из базы данных.
     *
     * @return множество выполненный миграций
     */
    private Set<String> getAppliedMigrations() {
        String query = "SELECT file_name FROM applied_migrations";
        Set<String> appliedMigrations = new HashSet<>();

        try(Connection connection = ConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while(resultSet.next()) {
                appliedMigrations.add(resultSet.getString("file_name"));
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("applied_migrations")) {
                throw new IllegalStateException("Table 'applied_migrations' not found. Ensure the database schema is initialized.", e);
            } else {
                throw new IllegalStateException("Error querying the database for applied migrations.", e);
            }
        }

        return appliedMigrations;
    }

    private long extractTimestamp(String fileName) {
        // Предполагаем, что имя файла начинается с временной метки формата YYYYMMDDHHMMSS
        try {
            String timestampPart = fileName.split("_")[0]; // Извлекаем часть до "_"
            return Long.parseLong(timestampPart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid migration file name format: " + fileName, e);
        }
    }
}
