package org.example;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrationFileReader {

    private static final String MIGRATION_FOLDER = "migrations"; // Папка с миграциями
    private static final String FILE_PATTERN = "\\d{14}_.*\\.sql"; // TODO определиться с форматом хранения

    /**
     * Читает и возвращает список миграций из папки ресурсов.
     *
     * @return Список объектов миграций, отсортированных по имени.
     * @throws IOException Если файлы не удалось прочитать.
     */
    public List<MigrationFile> readMigrationFiles() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(MIGRATION_FOLDER);

        if (resource == null) {
            throw new IllegalArgumentException("Migration folder not found: " + MIGRATION_FOLDER);
        }

        Path migrationPath;
        try {
            migrationPath = Paths.get(resource.toURI()); // Преобразуем URL в Path
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI for migration folder", e);
        }

        // Находим файлы и фильтруем их по шаблону
        List<Path> files = Files.list(migrationPath)
                .filter(path -> path.getFileName().toString().matches(FILE_PATTERN))
                .toList();

        // Преобразуем файлы в объекты MigrationFile и сортируем по имени
        return files.stream()
                .map(this::parseMigrationFile)
                .sorted(Comparator.comparing(MigrationFile::getFileName)) // Сортировка по имени файла
                .toList();
    }

    /**
     * Преобразует файл в объект MigrationFile.
     *
     * @param path Путь к SQL-файлу.
     * @return Объект MigrationFile.
     */
    private MigrationFile parseMigrationFile(Path path) {
        String fileName = path.getFileName().toString();
        try {
            String content = Files.readString(path);
            return new MigrationFile(fileName, content);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read migration file: " + fileName, e);
        }
    }
}
