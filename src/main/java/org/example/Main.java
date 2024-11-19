package org.example;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MigrationFileReader reader = new MigrationFileReader();
        try {
            List<MigrationFile> migrations = reader.readMigrationFiles();
            migrations.forEach(System.out::println); // Печатаем все миграции
        } catch (IOException e) {
            System.err.println("Error reading migration files: " + e.getMessage());
        }
    }
}
