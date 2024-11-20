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
                .sorted(Comparator.comparing(MigrationFile::getFileName))
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
}
