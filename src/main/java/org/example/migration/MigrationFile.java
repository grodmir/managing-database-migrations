package org.example.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс, представляющий миграционный файл.
 * Содержит имя файла и его содержимое.
 */
@Getter
@AllArgsConstructor
public class MigrationFile {
    /**
     * Имя миграционного файла.
     */
    private final String fileName;
    /**
     * Содержимое миграционного файла.
     */
    private final String content;

    /**
     * Возвращает строковое представление объекта {@link MigrationFile}.
     *
     * @return Строковое представление объекта в формате: MigrationFile{fileName='...', content='...'}
     */
    @Override
    public String toString() {
        return "MigrationFile{" +
                "fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
