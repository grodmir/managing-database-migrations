# Managing Database Migrations

## Описание
Managing Database Migrations — это библиотека для управления миграциями базы данных с использованием JDBC для PostgreSQL баз данных. Она позволяет автоматизировать процесс миграции схемы базы данных, обеспечивая простоту в подключении и использовании для разработчиков.

## Используемые технологии
- **Java**
- **Maven** (для сборки проекта)
- **JDBC** (для работы с базой данных)
- **Lombok** (для упрощения кода)
- **PostgreSQL Driver** (для подключения к PostgreSQL)
- **Logback** (для логирования)

## Установка

1. **Скачайте JAR файл** библиотеки и поместите его в вашу проектную директорию `libs`.
   
2. **Добавьте JAR файл как зависимость** в ваш проект. Для этого необходимо указать путь к файлу в настройках вашего проекта (например, в `pom.xml` для Maven):
   
   ```xml
   <dependency>
       <groupId>com.example</groupId>
       <artifactId>managing-database-migrations</artifactId>
       <version>1.0.0</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/libs/managing-database-migrations-1.0.0.jar</systemPath>
   </dependency>
   ```

3. **Создайте файл конфигурации** `migrations.properties` в корневой директории вашего проекта. В этом файле необходимо указать параметры для подключения к базе данных и путь к файлам миграций:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/mydatabase
   db.username=myuser
   db.password=mypassword
   db.driver=org.postgresql.Driver
   migration.path=path/to/migrations
   ```

   - **db.url** — URL для подключения к базе данных PostgreSQL.
   - **db.username** — имя пользователя для подключения.
   - **db.password** — пароль пользователя.
   - **migration.path** — путь к файлам миграций (относительный или абсолютный).

4. **Запустите процесс миграции** через класс `MigrationTool`. Для этого вызовите статический метод `run()`:

   ```java
    MigrationTool.run();
   ```

   Этот метод автоматически выполнит все миграции, которые находятся в указанном вами каталоге.

## Логирование
Все логированные сообщения будут сохраняться в файл `app.log`, который будет находиться в папке `logs` вашего проекта. Вы можете использовать этот лог для отслеживания процесса миграции и возможных ошибок.
