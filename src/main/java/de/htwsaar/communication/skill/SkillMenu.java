package de.htwsaar.communication.skill;

import java.util.List;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Skill;

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
        boolean back = false;

        while (!back) {
            out.section("Skills");
            System.out.println("1. Show all");
            System.out.println("2. Add");
            System.out.println("3. Search");
            System.out.println("4. Change");
            System.out.println("5. Return");

            switch (in.readMenuNumber(5)) {
                case 1 -> out.skillTable(skillService.findAllSkills());
                case 2 -> addSkill();
                case 3 -> searchSkill();
                case 4 -> changeSkill();
                case 5 -> back = true;
            }
        }
    }

    private void addSkill() {
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

    private void searchSkill() {
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
                skillService.findSkillByName(name).ifPresentOrElse(
                        s -> out.skillTable(List.of(s)),
                        () -> out.error("Skill not found.")
                );
            }
        }
    }

    private void changeSkill() {
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
