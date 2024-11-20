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
     */
    public List<MigrationFile> getPendingMigrations() throws IOException {
        List<MigrationFile> allMigrations = migrationFileReader.readMigrationFiles();
        Set<String> appliedMigrations = getAppliedMigrations();

        return allMigrations.stream()
                .filter(migration -> !appliedMigrations.contains(migration.getFileName()))
                .sorted(Comparator.comparing(migration -> extractTimestamp(migration.getFileName())))
                .collect(Collectors.toList());
    }

    /**
     * Загружает список выполненных миграций из базы данных.
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
            throw new RuntimeException("Failed to load applied migrations", e);
        }

        return appliedMigrations;
    }

    private long extractTimestamp(String fileName) {
        // Предполагаем, что имя файла начинается с временной метки формата YYYYMMDDHHMMSS
        try {
            String timestampPart = fileName.split("_")[0]; // Извлекаем часть до "_"
            return Long.parseLong(timestampPart); // Преобразуем в число для сортировки
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid migration file name format: " + fileName, e);
        }
    }
}
