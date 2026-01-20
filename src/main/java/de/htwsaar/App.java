package de.htwsaar;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.ConsoleUI;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.repository.EmployeeCertificateRepository;
import de.htwsaar.repository.EmployeeRepository;
import de.htwsaar.repository.EmployeeSkillRepository;
import de.htwsaar.repository.SQLConnection;
import de.htwsaar.repository.SkillRepository;
import de.htwsaar.util.DatabaseMigration;

import java.sql.Connection;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        DatabaseMigration.migrate();

        try (Connection conn = SQLConnection.open()) {

            // Repositories
            var empRepo = new EmployeeRepository(conn);
            var skillRepo = new SkillRepository(conn);
            var empSkillRepo = new EmployeeSkillRepository(conn);
            var certRepo = new CertificateRepository(conn);
            var empCertRepo = new EmployeeCertificateRepository(conn);

            // Services
            var empService = new EmployeeService(empRepo);
            var skillService = new SkillService(skillRepo, empSkillRepo);
            var certService = new CertificateService(certRepo, empCertRepo);

            // UI
            new ConsoleUI().start(empService, skillService, certService);

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
