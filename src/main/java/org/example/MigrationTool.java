package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
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
                log.info("Нет новых миграций для применения.");
            } else {
                log.info("Применение миграций:");
                // Выводим по порядку все миграции, которые должны быть выполнены
                migrations.forEach(m -> System.out.println(" - " + m.getFileName()));

                // Создаём экземпляр класса "исполнителя" миграций
                MigrationExecutor migrationExecutor = new MigrationExecutor();

                // Выполняем все необходимые миграции
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
            log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);;
        }
    }
}
