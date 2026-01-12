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
}
