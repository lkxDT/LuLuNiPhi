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
    public  Skill createSkill(String name, String category) {
        return skillRepo.save(name, category);
    }

    /**
     *
     * @return List of all entries in Skills
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
}
