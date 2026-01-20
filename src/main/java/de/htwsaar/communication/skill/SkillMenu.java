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
                case 2 -> SkillAddMenu.show(out, in, skillService);
                case 3 -> SkillSearchMenu.show(out, in, skillService);
                case 4 -> SkillChangeMenu.show(out, in, skillService);
                case 5 -> back = true;
            }
        }
    }
}
