package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;

import java.util.List;

public class SkillAddMenu {
    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        out.section("Add Skill");

        System.out.print("Skill name: ");
        String name = in.readLine();

        System.out.print("Category: ");
        String category = in.readLine();

        try {
            Skill created = skillService.createSkill(name, category);
            out.info("Skill created.");
            out.skillTable(List.of(created));
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }

}