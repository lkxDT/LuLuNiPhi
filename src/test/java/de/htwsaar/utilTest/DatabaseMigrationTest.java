package de.htwsaar.utilTest;

import de.htwsaar.util.DatabaseMigration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseMigrationTest {



    @Test
    @DisplayName("DatabaseMigration: Should run without exceptions")
    void testMigrateExecution() {
        assertDoesNotThrow(() -> {
            DatabaseMigration.migrate();
        }, "Migration should not throw an exception even if DB already exists");

        System.err.println("\u001B[32m✔ DatabaseMigration: Basic execution verified\u001B[0m");
    }

}
