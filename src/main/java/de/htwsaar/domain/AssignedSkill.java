package de.htwsaar.domain;

public class AssignedSkill extends Skill{

    private final int level;
    private final String sinceDate;

    public AssignedSkill(int id, String name, String category, int level, String sinceDate) {
        super(id, name, category);
        this.level = level;
        this.sinceDate = sinceDate;
    }

    public int getLevel() {
        return level;
    }

    public String getSinceDate() {
        return sinceDate;
    }
}