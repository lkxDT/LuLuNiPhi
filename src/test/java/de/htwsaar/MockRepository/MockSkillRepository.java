package de.htwsaar.MockRepository;

import de.htwsaar.domain.Skill;
import de.htwsaar.repository.SkillRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MockSkillRepository extends SkillRepository {
    private final List<Skill> skills = new ArrayList<>();

    public MockSkillRepository() {
        super(null);
        skills.add(new Skill(1, "Java", "Programming Language"));
        skills.add(new Skill(2, "SQL", "Database Query Language"));
    }


    @Override
    public Optional<Skill> findByName(String name) {
        if (name == null) return Optional.empty();
        return skills.stream()
            .filter(s -> s.getName().equalsIgnoreCase(name.trim()))
            .findFirst();
}

    @Override
    public List<Skill> findSkillsByName(String name) {
        String query = (name == null) ? "" : name.trim().toLowerCase();
        return skills.stream()
                .filter(s -> s.getName().toLowerCase().contains(query))
                .sorted(Comparator.comparingInt(Skill::getId))
                .toList();
    }


    @Override
    public List<Skill> findAll() {
        return new ArrayList<>(this.skills);
    }

    @Override
    public Skill save(String name, String description) {
        Skill newSkill = new Skill(skills.size() + 1, name, description);
        skills.add(newSkill);
        return newSkill;
    }
}