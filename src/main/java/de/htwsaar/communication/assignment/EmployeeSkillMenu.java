package de.htwsaar.communication.assignment;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class EmployeeSkillMenu {

    private final EmployeeService empService;
    private final SkillService skillService;
    private final InputReader in;
    private final OutputRenderer out;

    public EmployeeSkillMenu(
            EmployeeService empService,
            SkillService skillService,
            InputReader in,
            OutputRenderer out
    ) {
        this.empService = empService;
        this.skillService = skillService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Employee Skills");
            System.out.println("1. Assign skill");
            System.out.println("2. Show skills");
            System.out.println("3. Update assignment");
            System.out.println("4. Remove skill");
            System.out.println("5. Return");

            switch (in.readMenuNumber(5)) {
                case 1 -> EmployeeSkillAssignMenu.show(out, in, empService, skillService);
                case 2 -> EmployeeSkillShowMenu.show(out, in, skillService);
                case 3 -> EmployeeSkillUpdateMenu.show(out, in, skillService);
                case 4 -> EmployeeSkillRemoveMenu.show(out, in, skillService);
                case 5 -> back = true;
            }
        }
    }
}
