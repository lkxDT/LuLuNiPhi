package de.htwsaar.communication.skill;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class SkillMenu {

    private final SkillService skillService;
    private final InputReader in;
    private final OutputRenderer out;

    public SkillMenu(SkillService skillService, InputReader in, OutputRenderer out) {
        this.skillService = skillService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Skills");
            System.out.println("1. Show All");
            System.out.println("2. Search Skill");
            System.out.println("3. Add Skill");
            System.out.println("4. Change Skill");
            System.out.println("5. Delete Skill");
            System.out.println("6. Return");

            switch (in.readMenuNumber(6)) {
                case 1 -> out.skillTable(skillService.findAllSkills());
                case 2 -> SkillSearchMenu.show(out, in, skillService);
                case 3 -> SkillAddMenu.show(out, in, skillService);
                case 4 -> SkillChangeMenu.show(out, in, skillService);
                case 5 -> SkillDeleteMenu.show(out, in, skillService);
                case 6 -> back = true;
            }
        }
    }
}