package de.htwsaar.communication.employee;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.communication.employeeSkill.EmployeeSkillMenu;

public class EmployeeMenu {

    private final EmployeeService empService;
    private final SkillService skillService;
    private final CertificateService certService;
    private final InputReader in;
    private final OutputRenderer out;

    public EmployeeMenu(
            EmployeeService empService,
            SkillService skillService,
            CertificateService certService,
            InputReader in,
            OutputRenderer out
    ) {
        this.empService = empService;
        this.skillService = skillService;
        this.certService = certService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Employees");
            System.out.println("1. Show all");
            System.out.println("2. Add");
            System.out.println("3. Search");
            System.out.println("4. Change");
            System.out.println("5. Delete Employee");
            System.out.println("6. Employee Skills");
            System.out.println("7. Employee Certificates");
            System.out.println("8. Return");

            // ✅ du hast 8 Optionen -> max muss 8 sein
            switch (in.readMenuNumber(8)) {
                case 1 -> out.employeeTable(empService.findAllEmployees());
                case 2 -> EmployeeAddMenu.show(out, in, empService);
                case 3 -> EmployeeSearchMenu.show(out, in, empService);
                case 4 -> EmployeeChangeMenu.show(out, in, empService);
                case 5 -> EmployeeDeleteMenu.show(out, in, empService);
                case 6 -> new EmployeeSkillMenu(empService, skillService, in, out).show();
                case 7 -> EmployeeCertificateShowMenu.show(out, in, empService, certService);
                case 8 -> back = true;
            }
        }
    }
}