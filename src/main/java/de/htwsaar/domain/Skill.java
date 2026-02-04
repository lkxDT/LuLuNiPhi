package de.htwsaar.domain;

/**
 * Represents a skill that can be assigned to employees.
 * <p>
 * A skill consists of a name and a category and is used as
 * a read-only domain object.
 * This class is immutable.
 */
public class Skill {
    private final int id;
    private final String name;
    private final String category;

    /**
     * Creates a new skill.
     *
     * @param id        unique skill ID
     * @param name      name of the skill
     * @param category  category of the skill
     */
    public Skill(int id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    /**
     * Returns a formatted string representation of the skill,
     * intended for console output.
     *
     * @return formatted skill string
     */
    @Override
    public String toString() {
        return String.format("%4d | %25s | %25s", this.getId(), this.getName(), this.getCategory());
    }
}
