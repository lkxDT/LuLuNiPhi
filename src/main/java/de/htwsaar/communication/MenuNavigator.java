package de.htwsaar.communication;

import de.htwsaar.communication.employee.*;
import de.htwsaar.communication.skill.*;
import de.htwsaar.communication.certificate.*;
public class MenuNavigator {

    private boolean exit = false;

    public void mainLoop(
            EmployeeMenu empMenu,
            SkillMenu skillMenu,
            CertificateMenu certMenu,
            InputReader in,
            OutputRenderer out
    ) {
        while (!exit) {
            out.section("Main Menu");
            System.out.println("1. Employees");
            System.out.println("2. Skills");
            System.out.println("3. Certificates");
            System.out.println("4. Exit");

            switch (in.readMenuNumber(4)) {
                case 1 -> empMenu.show();
                case 2 -> skillMenu.show();
                case 3 -> certMenu.show();
                case 4 -> exit = true;
            }
        }
    }
}