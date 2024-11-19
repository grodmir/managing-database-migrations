//package org.example;
//
//import java.io.IOException;
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        MigrationFileReader reader = new MigrationFileReader();
//        try {
//            List<MigrationFile> migrations = reader.readMigrationFiles();
//            migrations.forEach(System.out::println); // Печатаем все миграции
//        } catch (IOException e) {
//            System.err.println("Error reading migration files: " + e.getMessage());
//        }
//    }
//}

package org.example;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = ConnectionManager.getConnection()) {
            System.out.println("Connected to the database successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
