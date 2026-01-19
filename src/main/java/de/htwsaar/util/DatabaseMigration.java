package de.htwsaar.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import de.htwsaar.repository.SQLConnection;

public class DatabaseMigration {

    private static final String DB_PATH = "data/employees.db";
    private static final String DB_URL  = "jdbc:sqlite:" + DB_PATH;

    public static void migrate() {
        try {
            Path path = Paths.get(DB_PATH);

            if (Files.exists(path)) {
                System.out.println("✔ Datenbank existiert bereits: " + DB_PATH);
                return;
            }

            Files.createDirectories(path.getParent());

            try (Connection conn = SQLConnection.open()) {
                System.out.println("✔ Neue Datenbank angelegt: " + DB_PATH);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Fehler bei der Datenbank-Migration", e);
        }
    }
}