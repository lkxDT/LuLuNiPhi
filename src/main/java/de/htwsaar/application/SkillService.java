package de.htwsaar.application;

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

    public  Skill createSkill(String name, String category) {
        return skillRepo.save(name, category);
    }

    public void assignSkill(int empId, int skillId, int level, String sinceDate) {
        employeeSkillRepo.assign(empId,skillId, level, sinceDate);
    }
}
