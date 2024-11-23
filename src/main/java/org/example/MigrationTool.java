package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class MigrationTool {
    public static void run() {
        try {
            MigrationTableManager.ensureMigrationTableExists();

            MigrationManager migrationManager = new MigrationManager(new MigrationFileReader());

            List<MigrationFile> migrations = migrationManager.getPendingMigrations();

            if (migrations == null || migrations.isEmpty()) {
                log.warn("Нет новых миграций для применения.");
            } else {
                log.info("Применение миграций:");
                migrations.forEach(m -> log.info(" - {}", m.getFileName()));

                MigrationExecutor migrationExecutor = new MigrationExecutor();

                try {
                    migrationExecutor.executeMigrations(migrations);
                    log.info("Все миграции успешно применены.");
                } catch (Exception e) {
                    log.error("Ошибка при выполнении миграций: {}", e.getMessage(), e);
                }
            }

            log.info("Процесс миграции завершён.");
        } catch (IOException e) {
            log.error("Ошибка во время настройки миграции: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);
        }
    }
}
