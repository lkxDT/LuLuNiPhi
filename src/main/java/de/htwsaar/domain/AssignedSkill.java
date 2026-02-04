package de.htwsaar.domain;

/**
 * Represents a skill that is assigned to an employee.
 * <p>
 * Extends {@link Skill} by adding assignment-specific
 * information such as proficiency level and the date
 * since the skill has been applied.
 */
public class AssignedSkill extends Skill{

    private final int level;
    private final String sinceDate;

    /**
     * Creates a new assigned skill instance.
     *
     * @param id        unique identifier of the skill
     * @param name      name of the skill
     * @param category  category of the skill
     * @param level     proficiency level (1–5)
     * @param sinceDate date since the skill is assigned
     */
    public AssignedSkill(int id, String name, String category, int level, String sinceDate) {
        super(id, name, category);
        this.level = level;
        this.sinceDate = sinceDate;
    }

    /**
     * @return the skill proficiency level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the date since the skill is assigned
     */
    public String getSinceDate() {
        return sinceDate;
    }
}