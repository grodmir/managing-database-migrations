package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class MigrationFileReader {

    private static final String MIGRATION_FOLDER = "migrations";
    private static final String FILE_PATTERN = "\\d{14}_.*\\.sql";

    /**
     * Читает и возвращает список миграций из папки ресурсов.
     *
     * @return Список объектов миграций, отсортированных по имени.
     * @throws IOException Если файлы не удалось прочитать.
     */
    public List<MigrationFile> readMigrationFiles() throws IOException {
        log.info("Начинаем чтение миграций из папки '{}'", MIGRATION_FOLDER);

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(MIGRATION_FOLDER);

        if (resource == null) {
            log.error("Папка миграций '{}' не найдена", MIGRATION_FOLDER);
            throw new IllegalArgumentException("Migration folder not found: " + MIGRATION_FOLDER);
        }

        Path migrationPath;
        try {
            migrationPath = Paths.get(resource.toURI()); // Преобразуем URL в Path
            log.debug("Путь к папке миграций: {}", migrationPath);
        } catch (URISyntaxException e) {
            log.error("Ошибка преобразования URI для папки миграций: {}", resource, e);
            throw new IOException("Failed to parse URI for migration folder: " + resource, e);
        }

        List<Path> files = Files.list(migrationPath)
                .filter(path -> path.getFileName().toString().matches(FILE_PATTERN))
                .toList();

        log.info("Найдено {} файлов миграций в папке '{}'", files.size(), MIGRATION_FOLDER);

        return files.stream()
                .map(this::parseMigrationFile)
                .sorted(Comparator.comparing(MigrationFile::getFileName))
                .toList();
    }

    private MigrationFile parseMigrationFile(Path path) {
        String fileName = path.getFileName().toString();
        log.debug("Читаем файл миграции: {}", fileName);

        try {
            String content = Files.readString(path);
            log.debug("Файл '{}' успешно прочитан, длина содержимого: {} символов", fileName, content.length());
            return new MigrationFile(fileName, content);
        } catch (IOException e) {
            log.error("Не удалось прочитать файл миграции: {}", fileName, e);
            throw new UncheckedIOException("Failed to read migration file: " + fileName, e);
        }
    }
}
