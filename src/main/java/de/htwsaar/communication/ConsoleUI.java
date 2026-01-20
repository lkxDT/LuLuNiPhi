package de.htwsaar.communication;

import java.util.Scanner;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.certificate.CertificateMenu;
import de.htwsaar.communication.employee.EmployeeMenu;
import de.htwsaar.communication.skill.SkillMenu;

public class ConsoleUI {

    public void start(
            EmployeeService empService,
            SkillService skillService,
            CertificateService certService
    ) {
        Scanner scanner = new Scanner(System.in);

        InputReader in = new InputReader(scanner);
        OutputRenderer out = new OutputRenderer();

        EmployeeMenu employeeMenu =
                new EmployeeMenu(empService, skillService, in, out);

        SkillMenu skillMenu =
                new SkillMenu(skillService, in, out);

        CertificateMenu certificateMenu =
                new CertificateMenu(empService, certService, in, out);

        MenuNavigator navigator = new MenuNavigator(
                employeeMenu,
                skillMenu,
                certificateMenu,
                in,
                out
        );

        out.printStartupBanner();
        navigator.run();
    }
}
