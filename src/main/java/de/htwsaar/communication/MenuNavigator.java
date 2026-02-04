package de.htwsaar.communication;

import de.htwsaar.communication.assignment.AssignmentsMenu;
import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;

/**
 * Handles navigation between the main application menus.
 * <p>
 * Displays the main menu and delegates control to the selected
 * sub-menus (employees, certificates, skills, assignments).
 * The navigator keeps running until the user chooses to exit.
 */
public class MenuNavigator {

    private final EmployeeMenu employeeMenu;
    private final CertificateMenu certificateMenu;
    private final SkillMenu skillMenu;
    private final AssignmentsMenu assignmentsMenu;

    private final InputReader in;
    private final OutputRenderer out;

    /**
     * Creates a new MenuNavigator with all required menus and utilities.
     *
     * @param employeeMenu menu for employee management
     * @param certificateMenu menu for certificate management
     * @param skillMenu menu for skill management
     * @param assignmentsMenu menu for skill and certificate assignments
     * @param in input reader for user input
     * @param out output renderer for console output
     */
    public MenuNavigator(
            EmployeeMenu employeeMenu,
            CertificateMenu certificateMenu,
            SkillMenu skillMenu,
            AssignmentsMenu assignmentsMenu,
            InputReader in,
            OutputRenderer out
    ) {
        this.employeeMenu = employeeMenu;
        this.certificateMenu = certificateMenu;
        this.skillMenu = skillMenu;
        this.assignmentsMenu = assignmentsMenu;
        this.in = in;
        this.out = out;
    }

    /**
     * Starts the main menu loop.
     * <p>
     * Continuously displays the main menu and routes the user
     * to the selected submenu until the exit option is chosen.
     */
    public void run() {
        boolean exit = false;

        while (!exit) {
            out.section("Main Menu");
            System.out.println("1. Employees");
            System.out.println("2. Certificates");
            System.out.println("3. Skills");
            System.out.println("4. Assignments");
            System.out.println("5. Exit");

            switch (in.readMenuNumber(5)) {
                case 1 -> employeeMenu.show();
                case 2 -> certificateMenu.show();
                case 3 -> skillMenu.show();
                case 4 -> assignmentsMenu.show();
                case 5 -> exit = true;
            }
        }
    }
}