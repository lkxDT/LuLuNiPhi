package de.htwsaar.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseMigration {

    private static final String DB_PATH = "data/app.db";
    private static final String DB_URL  = "jdbc:sqlite:" + DB_PATH;

    /**
     * Prüft, ob die DB existiert – falls nicht, wird sie angelegt.
     */
    public static void migrate() {
        try {
            Path path = Paths.get(DB_PATH);

            if (Files.exists(path)) {
                System.out.println("✔ Datenbank existiert bereits: " + DB_PATH);
                return;
            }

            Files.createDirectories(path.getParent());

            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                System.out.println("✔ Neue Datenbank angelegt: " + DB_PATH);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Fehler bei der Datenbank-Migration", e);
        }
    }
}


/** Database.migration.migrate() -> zur Main hinzufügen
 */