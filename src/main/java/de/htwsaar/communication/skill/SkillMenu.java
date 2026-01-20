package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.*;
import de.htwsaar.domain.Skill;

import java.util.List;

public class SkillMenu {
    private final SkillService skillService;
    private final InputReader in;
    private final OutputRenderer out;

    public SkillMenu(
            SkillService skillService,
            InputReader in,
            OutputRenderer out
    ) {
        this.skillService = skillService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        out.section("Skills");
        System.out.println("1. Show all");
        System.out.println("2. Add");
        System.out.println("3. Search");
        System.out.println("4. Change");
        System.out.println("6. Return");

        switch (in.readMenuNumber(5)) {
            case 1 -> out.skillTable(skillService.findAllSkills());
            case 2 -> {
                out.section("Add Skill");

                System.out.print("Skill name: ");
                String name = in.readLine();

                System.out.print("Category: ");
                String category = in.readLine();

                try {
                    Skill created = skillService.createSkill(name, category);
                    out.info("Skill successfully created.");
                    out.skillTable(List.of(created));
                } catch (RuntimeException e) {
                    out.error("Skill creation failed: " + e.getMessage());
                }
            }
            case 3 -> showSearch();
            case 4 -> {
                out.section("Change Skill");

                System.out.print("Enter skill ID to change: ");
                int id;
                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID. Please enter a whole number.");
                    return;
                }

                skillService.findSkillByID(id).ifPresentOrElse(skill -> {

                    out.info("Press Enter to keep current value.");

                    System.out.print("Skill name [" + skill.getName() + "]: ");
                    String newName = in.readLine();
                    if (newName.isBlank()) newName = skill.getName();

                    System.out.print("Category  [" + skill.getCategory() + "]: ");
                    String newCategory = in.readLine();
                    if (newCategory.isBlank()) newCategory = skill.getCategory();

                    Skill updated = new Skill(skill.getId(), newName.trim(), newCategory.trim());

                    try {
                        skillService.updateSkill(updated);
                        out.info("Skill updated successfully.");
                    } catch (RuntimeException e) {
                        out.error("Skill update failed: " + e.getMessage());
                    }

                }, () -> out.error("Skill with ID " + id + " not found."));
            }
            case 5 -> System.out.println("\n");
        }
    }

    public void showSearch() {
        out.section("Search Skill");
        System.out.println("1. By ID");
        System.out.println("2. By name");
        System.out.println("3. Return to skills");

        switch (in.readMenuNumber(3)) {
            case 1 -> {
                System.out.print("Enter ID: ");
                int id;
                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID.");
                    return;
                }

                skillService.findSkillByID(id).ifPresentOrElse(
                        skill -> out.skillTable(List.of(skill)),
                        () -> out.error("Skill with ID " + id + " not found.")
                );
            }

            case 2 -> {
                System.out.print("Enter name: ");
                String name = in.readLine().trim();

                skillService.findSkillByName(name).ifPresentOrElse(
                        skill -> out.skillTable(List.of(skill)),
                        () -> out.error("Skill with name '" + name + "' not found.")
                );
            }

            case 3 -> this.show();
        }
    }
}
