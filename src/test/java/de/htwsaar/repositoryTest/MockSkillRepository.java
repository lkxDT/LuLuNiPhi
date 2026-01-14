package de.htwsaar.repositoryTest;

import de.htwsaar.domain.Skill;
import de.htwsaar.repository.SkillRepository;

import java.util.ArrayList;
import java.util.List;

public class MockSkillRepository extends SkillRepository {
    private final List<Skill> skills = new ArrayList<>();

    public MockSkillRepository() {
        super(null);
        skills.add(new Skill(1, "Java", "Programming Language"));
        skills.add(new Skill(2, "SQL", "Database Query Language"));
    }


    @Override
    public Skill save(String name, String description) {
        Skill newSkill = new Skill(skills.size() + 1, name, description);
        skills.add(newSkill);
        return newSkill;
    }
}