package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;

public class SkillChangeMenu {
    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        out.section("Change Skill");

        System.out.print("Skill ID: ");
        int id;
        try {
            id = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        skillService.findSkillByID(id).ifPresentOrElse(skill -> {

            System.out.println("Press Enter to keep current value.");

            System.out.print("Name [" + skill.getName() + "]: ");
            String name = in.readLine();
            if (name.isBlank()) name = skill.getName();

            System.out.print("Category [" + skill.getCategory() + "]: ");
            String category = in.readLine();
            if (category.isBlank()) category = skill.getCategory();

            Skill updated = new Skill(skill.getId(), name.trim(), category.trim());

            try {
                skillService.updateSkill(updated);
                out.info("Skill updated.");
            } catch (RuntimeException e) {
                out.error(e.getMessage());
            }

        }, () -> out.error("Skill not found."));
    }
}
