package org.example.migration;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

/**
 * Класс для чтения миграционных файлов из указанной директории.
 */
@Slf4j
public class MigrationFileReader {
    private static final String FILE_PATTERN = "\\d{14}_.*\\.sql";
    private final String migrationFolderPath;

    /**
     * Конструктор для {@link MigrationFileReader}.
     * Инициализирует путь к папке миграций.
     */
    public MigrationFileReader() {
        this.migrationFolderPath = ConfigurationLoader.getInstance().getProperty("migration.path");
        log.info("Путь к папке миграций: {}", migrationFolderPath);
    }

    /**
     * Читает все миграционные файлы из указанной папки и возвращает их в виде списка объектов {@link MigrationFile}.
     * Файлы сортируются по имени.
     *
     * @return Список миграционных файлов.
     * @throws IOException Если не удаётся прочитать файлы.
     */
    public List<MigrationFile> readMigrationFiles() throws IOException {
        Path migrationPath = Paths.get(migrationFolderPath);
        log.info("Начинаем чтение миграций из папки '{}'", migrationPath);

        if (!Files.exists(migrationPath) || !Files.isDirectory(migrationPath)) {
            log.error("Папка миграций '{}' не существует или не является директорией.", migrationPath);
            throw new IllegalArgumentException("Migration folder does not exist or is not a directory: " + migrationPath);
        }

        List<Path> files = Files.list(migrationPath)
                .filter(path -> path.getFileName().toString().matches(FILE_PATTERN))
                .sorted()
                .toList();

        log.info("Найдено {} файлов миграций в папке '{}'", files.size(), migrationFolderPath);

        return files.stream()
                .map(this::parseMigrationFile)
                .sorted(Comparator.comparing(MigrationFile::getFileName))
                .toList();
    }

    /**
     * Читает файл миграции и создаёт объект {@link MigrationFile}.
     *
     * @param path Путь к файлу миграции.
     * @return Объект {@link MigrationFile} с содержимым файла.
     */
    private MigrationFile parseMigrationFile(Path path) {
        String fileName = path.getFileName().toString();
        log.debug("Читаем файл миграции: {}", fileName);

        try {
            String content = Files.readString(path);
            log.debug("Файл '{}' успешно прочитан, длина содержимого: {} символов", fileName, content.length());
            return new MigrationFile(fileName, content);
        } catch (IOException e) {
            log.error("Не удалось прочитать файл миграции: {}", fileName, e);
            throw new RuntimeException("Failed to read migration file: " + fileName, e);
        }
    }
}
