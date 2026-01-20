package de.htwsaar.communication.employeeSkill;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class EmployeeSkillAssignMenu {
    private EmployeeSkillAssignMenu () {}

    protected static void show(OutputRenderer out,
                             InputReader in,
                             EmployeeService empService,
                             SkillService skillService
    ) {
        System.out.print("Employee ID: ");
        int empId;
        try {
            empId = in.readInt("Invalid employee ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        if (empService.findEmployeeByID(empId).isEmpty()) {
            out.error("Employee not found.");
            return;
        }

        out.skillTable(skillService.findAllSkills());

        System.out.print("Skill ID: ");
        int skillId;
        try {
            skillId = in.readInt("Invalid skill ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        System.out.print("Level (1-5): ");
        int level;
        try {
            level = in.readInt("Invalid level.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        System.out.print("Since date (yyyy-mm-dd): ");
        String since = in.readLine();

        try {
            skillService.assignSkill(empId, skillId, level, since);
            out.info("Skill assigned.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}
