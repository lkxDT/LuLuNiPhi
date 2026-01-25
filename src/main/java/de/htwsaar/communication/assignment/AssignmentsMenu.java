package de.htwsaar.communication.assignment;

import de.htwsaar.application.CertificateService;
import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class AssignmentsMenu {

    private final EmployeeService empService;
    private final SkillService skillService;
    private final CertificateService certService;
    private final InputReader in;
    private final OutputRenderer out;

    public AssignmentsMenu(
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
            out.section("Assignments");
            System.out.println("1. Show Employee Certificates");
            System.out.println("2. Show Employee Skills");
            System.out.println("3. Assign Certificate");
            System.out.println("4. Assign Skill");
            System.out.println("5. Update Certificate Assignment");
            System.out.println("6. Update Skill Assignment");
            System.out.println("7. Remove Certificate Assignment");
            System.out.println("8. Remove Skill Assignment");
            System.out.println("9. Return");

            switch (in.readMenuNumber(9)) {
                case 1 -> EmployeeCertificateShowMenu.show(out, in, empService, certService);
                case 2 -> EmployeeSkillShowMenu.show(out, in, skillService);
                case 3 -> CertificateAssignMenu.show(out, in, empService, certService);
                case 4 -> EmployeeSkillAssignMenu.show(out, in, empService, skillService);
                case 5 -> CertificateAssignmentUpdateMenu.show(out, in, empService, certService);
                case 6 -> EmployeeSkillUpdateMenu.show(out, in, skillService);
                case 7 -> CertificateAssignmentRemoveMenu.show(out, in, empService, certService);
                case 8 -> EmployeeSkillRemoveMenu.show(out, in, skillService);
                case 9 -> back = true;
            }
        }
    }
}