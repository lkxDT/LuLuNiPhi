package de.htwsaar.communication;

import de.htwsaar.communication.assignment.AssignmentsMenu;
import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;

public class MenuNavigator {

    private final EmployeeMenu employeeMenu;
    private final CertificateMenu certificateMenu;
    private final SkillMenu skillMenu;
    private final AssignmentsMenu assignmentsMenu;

    private final InputReader in;
    private final OutputRenderer out;

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