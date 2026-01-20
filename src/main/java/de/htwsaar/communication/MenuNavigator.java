package de.htwsaar.communication;

import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;

public class MenuNavigator {

    private final EmployeeMenu employeeMenu;
    private final SkillMenu skillMenu;
    private final CertificateMenu certificateMenu;
    private final InputReader in;
    private final OutputRenderer out;

    public MenuNavigator(
            EmployeeMenu employeeMenu,
            SkillMenu skillMenu,
            CertificateMenu certificateMenu,
            InputReader in,
            OutputRenderer out
    ) {
        this.employeeMenu = employeeMenu;
        this.skillMenu = skillMenu;
        this.certificateMenu = certificateMenu;
        this.in = in;
        this.out = out;
    }

    public void run() {
        boolean exit = false;

        while (!exit) {
            out.section("Main Menu");
            System.out.println("1. Employees");
            System.out.println("2. Skills");
            System.out.println("3. Certificates");
            System.out.println("4. Exit");

            switch (in.readMenuNumber(4)) {
                case 1 -> employeeMenu.show();
                case 2 -> skillMenu.show();
                case 3 -> certificateMenu.show();
                case 4 -> exit = true;
            }
        }
    }
}
