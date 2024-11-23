package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MigrationFile {
    private final String fileName;  // Имя файла миграции
    private final String content;  // Содержимое файла

    @Override
    public String toString() {
        return "MigrationFile{" +
                "fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
