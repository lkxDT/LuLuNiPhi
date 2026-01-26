package de.htwsaar.communication.employeeSkill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedSkill;

import java.util.List;

public class EmployeeSkillUpdateMenu {
    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
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
                .filter(s -> s.getId() == skillId)
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
