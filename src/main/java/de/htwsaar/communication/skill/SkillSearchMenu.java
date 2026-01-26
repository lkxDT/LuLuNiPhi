package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

import java.util.List;

public class SkillSearchMenu {
    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        out.section("Search Skill");
        System.out.println("1. By ID");
        System.out.println("2. By name");
        System.out.println("3. Return");

        switch (in.readMenuNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                try {
                    int id = in.readInt("Invalid ID.");
                    skillService.findSkillByID(id).ifPresentOrElse(
                            s -> out.skillTable(List.of(s)),
                            () -> out.error("Skill not found.")
                    );
                } catch (IllegalArgumentException e) {
                    out.error(e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("Enter name: ");
                String name = in.readLine();
                out.skillTable(skillService.findSkillsByName(name));
            }
        }
    }
}
