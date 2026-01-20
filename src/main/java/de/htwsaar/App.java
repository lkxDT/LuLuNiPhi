package de.htwsaar;

import de.htwsaar.application.*;
import de.htwsaar.communication.*;
import de.htwsaar.repository.*;
import org.sqlite.SQLiteConnection;
import de.htwsaar.util.*;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.application.CertificateService;
import de.htwsaar.domain.Certificate;

import java.sql.Connection;

import java.sql.SQLException;

public class App 
{
    public static void main( String[] args )
    {
        DatabaseMigration.migrate();

        try {
            Connection conn = SQLConnection.open();

            var empRepo = new EmployeeRepository(conn);
            var skillRepo = new SkillRepository(conn);
            var empSkillRepo = new EmployeeSkillRepository(conn);
            var certRepo = new CertificateRepository(conn);

            var empService = new EmployeeService(empRepo);
            var skillService = new SkillService(skillRepo, empSkillRepo);
            var certService = new CertificateService(certRepo);

            var userIn = new ConsoleUI();
            userIn.start(empService, skillService, certService);

            //userIn.showStartupBanner();
            //userIn.startMainMenu();

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
