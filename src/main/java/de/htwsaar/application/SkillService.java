package de.htwsaar.application;

import java.util.List;
import java.util.Optional;

import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import de.htwsaar.repository.EmployeeSkillRepository;
import de.htwsaar.repository.SkillRepository;

public class SkillService {
    private final SkillRepository skillRepo;
    private final EmployeeSkillRepository employeeSkillRepo;

    public SkillService(SkillRepository s, EmployeeSkillRepository es) {
        this.skillRepo = s;
        this.employeeSkillRepo = es;
    }

    /**
     *
     * @param name Name of skill
     * @param category Category of skill
     * @return DomainObject of type Skill
     */
    public List<Skill> findAllSkills() {
        return skillRepo.findAll();
    }

    public Optional<Skill> findSkillByID(int id) {
        return skillRepo.findByID(id);
    }

    public Optional<Skill> findSkillByName(String name) {
        return skillRepo.findByName(name);
    }

    public void assignSkill(int empId, int skillId, int level, String sinceDate) {
        employeeSkillRepo.assign(empId,skillId, level, sinceDate);
    }

    public Skill createSkill(String name, String category) {
        if (invalid(name) || invalid(category)) {
            throw new IllegalArgumentException("Skill name and category must be filled out.");
        }
        return skillRepo.save(name.trim(), category.trim());
    }

    public void updateSkill(Skill skill) {
        if (invalid(skill.getName()) || invalid(skill.getCategory())) {
            throw new IllegalArgumentException("Skill name and category must be filled out.");
        }
        skillRepo.update(skill);
    }

    private boolean invalid(String v) {
        return v == null || v.isBlank();
    }
}
