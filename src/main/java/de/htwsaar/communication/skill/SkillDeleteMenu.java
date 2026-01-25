package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class SkillDeleteMenu {
    private SkillDeleteMenu() {}

    public static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        out.section("Delete Skill");

        System.out.print("Skill ID: ");
        int id;
        try {
            id = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        var skillOpt = skillService.findSkillByID(id);
        if (skillOpt.isEmpty()) {
            out.error("Skill with ID " + id + " not found.");
            return;
        }

        out.skillTable(java.util.List.of(skillOpt.get()));

        System.out.print("Are you sure you want to delete this skill? (y/N): ");
        String confirm = in.readLine();
        if (!confirm.equalsIgnoreCase("y")) {
            out.info("Delete canceled.");
            return;
        }

        try {
            skillService.deleteSkillById(id);
            out.info("Skill deleted.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}