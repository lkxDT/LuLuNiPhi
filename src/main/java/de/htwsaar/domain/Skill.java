package de.htwsaar.domain;

public class Skill {
    private final int id;
    private final String name;
    public final String category;

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

    @Override
    public String toString() {
        return String.format("%4d | %25s | %25s", this.getId(), this.getName(), this.getCategory());
    }
}
