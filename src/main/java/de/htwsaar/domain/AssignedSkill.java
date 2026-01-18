package de.htwsaar.domain;

public class AssignedSkill {
    private final int skillId;
    private final String name;
    private final String category;
    private final int level;
    private final String sinceDate;

    public AssignedSkill(int skillId, String name, String category, int level, String sinceDate) {
        this.skillId = skillId;
        this.name = name;
        this.category = category;
        this.level = level;
        this.sinceDate = sinceDate;
    }

    public int getSkillId() {
        return skillId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getLevel() {
        return level;
    }

    public String getSinceDate() {
        return sinceDate;
    }
}