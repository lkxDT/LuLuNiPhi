package de.htwsaar.communication;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;

import de.htwsaar.communication.assignment.AssignmentsMenu;
import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;

/**
 * Entry point for the console-based user interface.
 * <p>
 * Initializes input/output handling, configures logging,
 * creates all main menus and starts the menu navigation loop.
 */
public class ConsoleUI {

    /**
     * Starts the console UI.
     * <p>
     * Sets up system properties and logging, initializes shared
     * {@link InputReader} and {@link OutputRenderer} instances,
     * creates all menus and hands control over to the
     * {@link MenuNavigator}.
     *
     * @param empService   EmployeeService for employee-related operations
     * @param skillService SkillService for skill-related operations
     * @param certService  CertificateService for certificate-related operations
     */
    public void start(
            EmployeeService empService,
            SkillService skillService,
            CertificateService certService
    ) {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
        Logger jooqLogger = Logger.getLogger("org.jooq");
        jooqLogger.setLevel(Level.WARNING);

        Scanner scanner = new Scanner(System.in);

        InputReader in = new InputReader(scanner);
        OutputRenderer out = new OutputRenderer();

        EmployeeMenu employeeMenu = new EmployeeMenu(empService, in, out);
        CertificateMenu certificateMenu = new CertificateMenu(certService, in, out);
        SkillMenu skillMenu = new SkillMenu(skillService, in, out);

        AssignmentsMenu assignmentsMenu = new AssignmentsMenu(
                empService, skillService, certService, in, out
        );

        MenuNavigator navigator = new MenuNavigator(
                employeeMenu,
                certificateMenu,
                skillMenu,
                assignmentsMenu,
                in,
                out
        );

        out.printStartupBanner();
        navigator.run();
    }
}