package org.example;

import java.io.IOException;
import java.util.List;

public class MigrationTool {
    public static void run() {
        try {
            // Проверяет (если нет, создаёт) таблицу для хранения выполненных миграций
            MigrationTableManager.ensureMigrationTableExists();

            // Создаём менеджер и передаём в него экземпляр класса для работы с файлами миграций
            MigrationManager migrationManager = new MigrationManager(new MigrationFileReader());

            // Получаем список миграций, которые необходимо выполнить
            List<MigrationFile> migrations = migrationManager.getPendingMigrations();

            // Обрабатываем случай, если список миграций оказался null или пустым
            if (migrations == null || migrations.isEmpty()) {
                System.out.println("No new migrations to apply.");
            } else {
                System.out.println("Applying migrations:");
                // Выводим по порядку все миграции, которые должны быть выполнены
                migrations.forEach(m -> System.out.println(" - " + m.getFileName()));

                // Создаём экземпляр класса "исполнителя" миграций
                MigrationExecutor migrationExecutor = new MigrationExecutor();

                // Выполняем все необходимые миграции
                try {
                    migrationExecutor.executeMigrations(migrations);
                } catch (Exception e) {
                    System.err.println("Error while executing migrations: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Migration process completed.");
        } catch (IOException e) {
            System.err.println("Error during migration setup: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
