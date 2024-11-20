package org.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // Этап 1: Убедимся, что таблица для выполненных миграций существует
        MigrationTableManager.ensureMigrationTableExists();
        MigrationFileReader migrationFileReader = new MigrationFileReader();
        MigrationManager migrationManager = new MigrationManager(migrationFileReader);

        List<MigrationFile> migrations = migrationManager.getPendingMigrations();

        if (migrations.isEmpty()) {
            System.out.println("No new migrations to apply.");
        } else {
            System.out.println("Applying migrations:");
            migrations.forEach(m -> System.out.println(" - " + m.getFileName()));
            MigrationExecutor migrationExecutor = new MigrationExecutor();

            migrationExecutor.executeMigrations(migrations);
        }

        System.out.println("Migration process completed.");
    }
}