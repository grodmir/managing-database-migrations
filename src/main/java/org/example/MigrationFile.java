package org.example;

public class MigrationFile {
    private final String fileName;  // Имя файла миграции
    private final String content;  // Содержимое файла

    public MigrationFile(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MigrationFile{" +
                "fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
