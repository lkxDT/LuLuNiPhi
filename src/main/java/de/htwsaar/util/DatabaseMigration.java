package de.htwsaar.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import de.htwsaar.repository.SQLConnection;

public class DatabaseMigration {

    private static final String DB_PATH = SQLConnection.DB_PATH;

    public static void migrate() {
        try {
            Path path = Paths.get(DB_PATH);


            if (Files.exists(path)) {

                System.out.println("✔ Datenbank existiert bereits: " + DB_PATH);
                return;
            }

            Files.createDirectories(path.getParent());

            String createEmployeeTable = "CREATE TABLE \"employee\" (\n" +
                    "\t\"id\"\tINTEGER,\n" +
                    "\t\"first_Name\"\tTEXT NOT NULL,\n" +
                    "\t\"last_name\"\tTEXT NOT NULL,\n" +
                    "\t\"mail\"\tNUMERIC NOT NULL,\n" +
                    "\t\"role\"\tTEXT NOT NULL,\n" +
                    "\t\"status\"\tTEXT NOT NULL,\n" +
                    "\t\"hire_date\"\tTEXT NOT NULL,\n" +
                    "\t\"working_hours\"\tREAL NOT NULL,\n" +
                    "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                    ")";

            String createSkillTable = "CREATE TABLE \"employee_skill\" (\n" +
                    "\t\"employee_id\"\tINTEGER NOT NULL,\n" +
                    "\t\"skill_id\"\tINTEGER NOT NULL,\n" +
                    "\t\"level\"\tINTEGER,\n" +
                    "\t\"since_date\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"employee_id\",\"skill_id\"),\n" +
                    "\tCONSTRAINT \"fk_employee_id\" FOREIGN KEY(\"employee_id\") REFERENCES \"employee\"(\"id\") ON DELETE CASCADE,\n" +
                    "\tCONSTRAINT \"fk_skill_id\" FOREIGN KEY(\"skill_id\") REFERENCES \"skill\"(\"id\") ON DELETE CASCADE\n" +
                    ")";

            String createEmployeeSkillTable = "CREATE TABLE \"skill\" (\n" +
                    "\t\"id\"\tINTEGER,\n" +
                    "\t\"skill_name\"\tTEXT NOT NULL,\n" +
                    "\t\"category\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                    ")";

            String createCertificateTable = "CREATE TABLE certificate (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  name TEXT NOT NULL UNIQUE,\n" +
                    "  category TEXT NOT NULL,\n" +
                    "  issuer_type TEXT NOT NULL,\n" +
                    "  requires_renewal INTEGER NOT NULL,\n" +
                    "  renewal_cycle_months INTEGER,\n" +
                    "  description TEXT\n" +
                    ")";

            String createEmployeeCertificateTable = "CREATE TABLE employee_certificate (\n" +
                    "  employee_id INTEGER NOT NULL,\n" +
                    "  certificate_id INTEGER NOT NULL,\n" +
                    "  valid_from TEXT,\n" +
                    "  valid_until TEXT,\n" +
                    "  certificate_number TEXT,\n" +
                    "  status TEXT NOT NULL DEFAULT 'ACTIVE',\n" +
                    "  PRIMARY KEY (employee_id, certificate_id),\n" +
                    "  FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,\n" +
                    "  FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE\n" +
                    ")";

            try (Connection conn = SQLConnection.open()) {


                var stmt = conn.createStatement();

                stmt.executeUpdate(createEmployeeTable);
                stmt.executeUpdate(createSkillTable);
                stmt.executeUpdate(createCertificateTable);
                stmt.executeUpdate(createEmployeeSkillTable);
                stmt.executeUpdate(createEmployeeCertificateTable);

                System.out.println("✔ Neue Datenbank angelegt: " + DB_PATH);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ Fehler bei der Datenbank-Migration", e);
        }
    }

}