package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class MigrationFileReader {
    private static final String FILE_PATTERN = "\\d{14}_.*\\.sql";
    private final String migrationFolderPath;

    public MigrationFileReader() {
        this.migrationFolderPath = ConfigurationLoader.getInstance().getProperty("migration.path");
        log.info("Путь к папке миграций: {}", migrationFolderPath);
    }

    /**
     * Читает и возвращает список миграций из указанной папки.
     *
     * @return Список объектов миграций, отсортированных по имени.
     * @throws IOException Если файлы не удалось прочитать.
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
     * Читает файл миграции и создаёт объект MigrationFile.
     *
     * @param path Путь к файлу миграции.
     * @return Объект MigrationFile с содержимым файла.
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
