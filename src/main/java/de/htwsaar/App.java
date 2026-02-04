package de.htwsaar;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.ConsoleUI;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.repository.EmployeeCertificateRepository;
import de.htwsaar.repository.EmployeeRepository;
import de.htwsaar.repository.EmployeeSkillRepository;
import de.htwsaar.repository.SkillRepository;
import de.htwsaar.util.DatabaseMigration;
import de.htwsaar.util.JooqContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Application entry point.
 * <p>
 * Initializes the database, repositories, services and
 * starts the console-based user interface.
 */
public class App {

    /**
     * Starts the application.
     * <p>
     * Accepts an optional database file path as command-line argument.
     * If no path is provided, the default database is created and migrated.
     * Initializes repositories, services and launches the console UI.
     *
     * @param args optional command-line arguments (database path)
     */
    public static void main(String[] args) {
        String dbPath= "data/employees.db";
        if(args.length == 1) {
            dbPath = args[0];
        }else if(args.length > 1) {
            System.out.println("Usage: java -jar LuLuNiPhi-2.0 <DB-PATH>");
            System.exit(1);
        }else {
            DatabaseMigration.migrate();
        }

        try (Connection conn = JooqContext.openConnection(dbPath)) {

            var empRepo = new EmployeeRepository(conn);
            var skillRepo = new SkillRepository(conn);
            var empSkillRepo = new EmployeeSkillRepository(conn);
            var certRepo = new CertificateRepository(conn);
            var empCertRepo = new EmployeeCertificateRepository(conn);

            var empService = new EmployeeService(empRepo);
            var skillService = new SkillService(skillRepo, empSkillRepo);
            var certService = new CertificateService(certRepo, empCertRepo);

            new ConsoleUI().start(empService, skillService, certService);

        } catch (SQLException e) {
            System.err.println("Database not found. Please restart and enter a valid path to a database.");
        }
    }
}