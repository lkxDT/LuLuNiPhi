package de.htwsaar.communication.employee;

import java.util.List;

import de.htwsaar.application.EmployeeService;
import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedSkill;

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
            System.out.println("4. Return");

            switch (in.readMenuNumber(4)) {
                case 1 -> assign();
                case 2 -> showSkills();
                case 3 -> update();
                case 4 -> back = true;
            }
        }
    }

    private void assign() {
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

    private void showSkills() {
        System.out.print("Employee ID: ");
        try {
            int empId = in.readInt("Invalid ID.");
            List<AssignedSkill> skills =
                    skillService.findSkillsOfEmployee(empId);
            out.assignedSkillTable(skills);
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
        }
    }

    private void update() {
        System.out.print("Employee ID: ");
        int empId;
        try {
            empId = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        List<AssignedSkill> skills =
                skillService.findSkillsOfEmployee(empId);

        if (skills.isEmpty()) {
            out.info("No skills assigned.");
            return;
        }

        out.assignedSkillTable(skills);

        System.out.print("Skill ID: ");
        int skillId;
        try {
            skillId = in.readInt("Invalid skill ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        AssignedSkill existing = skills.stream()
                .filter(s -> s.getSkillId() == skillId)
                .findFirst()
                .orElse(null);

        if (existing == null) {
            out.error("Skill not assigned.");
            return;
        }

        System.out.print("Level [" + existing.getLevel() + "]: ");
        String lvl = in.readLine();
        int newLevel = lvl.isBlank() ? existing.getLevel() : Integer.parseInt(lvl);

        System.out.print("Since [" + existing.getSinceDate() + "]: ");
        String since = in.readLine();
        if (since.isBlank()) since = existing.getSinceDate();

        try {
            skillService.updateSkillAssignment(empId, skillId, newLevel, since);
            out.info("Skill updated.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}
