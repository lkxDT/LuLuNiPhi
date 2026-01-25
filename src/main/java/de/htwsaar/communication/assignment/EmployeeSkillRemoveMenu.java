package de.htwsaar.communication.assignment;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedSkill;

import java.util.List;

public class EmployeeSkillRemoveMenu {

    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        System.out.print("Employee ID: ");
        int empId;
        try {
            empId = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        List<AssignedSkill> skills = skillService.findSkillsOfEmployee(empId);

        if (skills.isEmpty()) {
            out.info("No skills assigned.");
            return;
        }

        out.assignedSkillTable(skills);

        System.out.print("Skill ID to remove: ");
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

        System.out.print("Are you sure you want to remove this skill? (y/N): ");
        String confirm = in.readLine();
        if (!confirm.equalsIgnoreCase("y")) {
            out.info("Removal canceled.");
            return;
        }

        try {
            skillService.removeSkillFromEmployee(empId, skillId);
            out.info("Skill removed from employee.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}
