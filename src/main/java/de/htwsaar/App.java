package de.htwsaar;

import de.htwsaar.application.*;
import de.htwsaar.communication.UI;
import de.htwsaar.repository.*;
import org.sqlite.SQLiteConnection;
import de.htwsaar.util.*;

import java.sql.Connection;

import java.sql.SQLException;

/**
 * Hello world!
 *
 */
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

            var empService = new EmployeeService(empRepo);
            var skillService = new SkillService(skillRepo, empSkillRepo);
            var userIn = new UI(empService, skillService);
            userIn.startMainMenu();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
