package de.htwsaar.domain;

public class EmployeeSkill {
    private final int employeeId;
    private final int skillId;
    private final int level;
    private final String sinceDate;

    public EmployeeSkill(int employeeId, int skillId, int level, String sinceDate) {
        this.employeeId = employeeId;
        this.skillId = skillId;
        this.level = level;
        this.sinceDate = sinceDate;
    }

    public int getEmployeeId() { return employeeId; }
    public int getSkillId() { return skillId; }
    public int getLevel() { return level; }
    public String getSinceDate() { return sinceDate; }
}


