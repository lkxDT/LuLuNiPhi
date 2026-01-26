package de.htwsaar.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static de.htwsaar.util.JooqContext.DB_PATH;

public class DatabaseMigration {

    private static final String DB_PATH = "data/employees.db";

    public static void migrate() {
        try {
            Path db = Paths.get(DB_PATH);

            // Ordner sicherstellen
            Path parent = db.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            // DB muss im Projekt vorhanden sein
            if (!Files.exists(db)) {
                throw new IllegalStateException(
                        "Datenbankdatei fehlt: " + DB_PATH + "\n" +
                                "Bitte stelle sicher, dass die Datei im Projekt enthalten ist (z.B. im Ordner 'data')."
                );
            }

            System.out.println("✔ Datenbank gefunden: " + DB_PATH);

        } catch (Exception e) {
            throw new RuntimeException("❌ DB-Check fehlgeschlagen", e);
        }
    }
}