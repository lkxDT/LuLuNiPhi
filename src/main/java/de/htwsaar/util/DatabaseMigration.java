package de.htwsaar.util;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import static org.jooq.impl.DSL.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseMigration {

    private static final String DB_PATH = "data/employees.db";


    public static void migrate() {
        DBFileHandler.createDbFile(DB_PATH);

        //Check if Database already exists
        if (Files.exists(Paths.get("data/employees.db"))) {
            System.out.println("Datenbak existiert bereits");
            return;
        }

        //Get the connection to JDBC
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:"+DB_PATH)) {
            DSLContext create = DSL.using(conn, SQLDialect.SQLITE);

            //Employee table creation
            create.createTableIfNotExists("employee")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("first_Name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("last_name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("mail", SQLDataType.VARCHAR(255).nullable(false))
                    .column("role", SQLDataType.VARCHAR(255).nullable(false))
                    .column("status", SQLDataType.VARCHAR(255).nullable(false))
                    .column("hire_date", SQLDataType.VARCHAR(255).nullable(false))
                    .column("working_hours", SQLDataType.REAL.nullable(false))
                    .constraints(
                            constraint("pk_employee").primaryKey("id")
                    )
                    .execute();

            //Certificate table creation
            create.createTableIfNotExists("certificate")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("category", SQLDataType.VARCHAR(255).nullable(false))
                    .column("issuer_type", SQLDataType.VARCHAR(255).nullable(false))
                    .column("requires_renewal", SQLDataType.INTEGER.nullable(false))
                    .column("renewal_cycle_months", SQLDataType.INTEGER)
                    .column("description", SQLDataType.VARCHAR(255))
                    .constraints(
                            constraint("pk_certificate").primaryKey("id"),
                            constraint("uk_certificate_name").unique("name")
                    )
                    .execute();

            //Skill table creation
            create.createTableIfNotExists("skill")
                    .column("id", SQLDataType.INTEGER.identity(true))
                    .column("skill_name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("category", SQLDataType.VARCHAR(255).nullable(false))
                    .constraints(
                            constraint("pk_skill").primaryKey("id")
                    )
                    .execute();

            //EmployeeCertificate table creation
            create.createTableIfNotExists("employee_certificate")
                    .column("employee_id", SQLDataType.INTEGER.nullable(false))
                    .column("certificate_id", SQLDataType.INTEGER.nullable(false))
                    .column("valid_from", SQLDataType.VARCHAR(255))
                    .column("valid_until", SQLDataType.VARCHAR(255))
                    .column("certificate_number", SQLDataType.VARCHAR(255))
                    .column("status", SQLDataType.VARCHAR.length(255).nullable(false))
                    .constraints(
                            constraint("pk_employee_certificate").primaryKey("employee_id", "certificate_id"),
                            constraint("fk_employee_certificate_employee").foreignKey("employee_id").references("employee", "id").onDeleteCascade(),
                            constraint("fk_employee_certificate_certificate").foreignKey("certificate_id").references("certificate", "id").onDeleteCascade()
                    )
                    .execute();

            //EmployeeSkill table creation
            create.createTableIfNotExists("employee_skill")
                    .column("employee_id", SQLDataType.INTEGER.nullable(false))
                    .column("skill_id", SQLDataType.INTEGER.nullable(false))
                    .column("level", SQLDataType.INTEGER)
                    .column("since_date", SQLDataType.VARCHAR)
                    .constraints(
                            constraint("pk_employee_skill").primaryKey("employee_id", "skill_id"),
                            constraint("fk_employee_id").foreignKey("employee_id").references("employee", "id").onDeleteCascade(),
                            constraint("fk_skill_id").foreignKey("skill_id").references("skill", "id").onDeleteCascade()
                    )
                    .execute();
            System.out.println("New Database created at "+ DB_PATH);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Database");
        }




    }
    /*public static void migrate() {


    CREATE TABLE certificate (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL UNIQUE,
  category TEXT NOT NULL,
  issuer_type TEXT NOT NULL,
  requires_renewal INTEGER NOT NULL,
  renewal_cycle_months INTEGER,
  description TEXT
)







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
    }*/
}