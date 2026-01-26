package de.htwsaar.application;

import java.util.List;
import java.util.Optional;
import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.domain.Skill;
import de.htwsaar.repository.EmployeeSkillRepository;
import de.htwsaar.repository.SkillRepository;
import de.htwsaar.util.DateFormatChecker;

public class SkillService {

    private final SkillRepository skillRepo;
    private final EmployeeSkillRepository employeeSkillRepo;

    public SkillService(SkillRepository s, EmployeeSkillRepository es) {
        this.skillRepo = s;
        this.employeeSkillRepo = es;
    }

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
        validateSkillLevel(level);
        if (employeeSkillRepo.isNotAssigned(empId, skillId)) {
            throw new IllegalStateException(
                    "This skill is already assigned to the employee."
            );
        }

        if (!DateFormatChecker.check(sinceDate)) {
            throw new IllegalArgumentException(
                    "The given date is not a valid date"
            );
        }

        employeeSkillRepo.assign(empId, skillId, level, sinceDate);
    }

    public void updateSkillAssignment(int empId, int skillId, int level, String sinceDate) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5.");
        }
        /*if (sinceDate == null || sinceDate.isBlank()) {
            throw new IllegalArgumentException("Since date must not be empty.");
        }*/
        if (!DateFormatChecker.check(sinceDate)) {
            throw new IllegalArgumentException(
                    "The given date is not a valid date"
            );
        }

        employeeSkillRepo.updateAssignment(empId, skillId, level, sinceDate);
    }

    public void removeSkillFromEmployee(int empId, int skillId) {

        if (employeeSkillRepo.isNotAssigned(empId, skillId)) {
            throw new IllegalStateException("This skill is not assigned to the employee.");
        }

        employeeSkillRepo.removeAssignment(empId, skillId);
    }

    public Skill createSkill(String name, String category) {
        validateNotEmpty(name);
        validateNotEmpty(category);
        if(skillRepo.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Skill already exists");
        }
        return skillRepo.save(name.trim(), category.trim());
    }

    public void updateSkill(Skill skill) {
        validateNotEmpty(skill.getName());
        validateNotEmpty(skill.getCategory());
        skillRepo.update(skill);
    }

    public List<AssignedSkill> findSkillsOfEmployee(int empId) {
        return employeeSkillRepo.findSkillsByEmployeeId(empId);
    }


    private void validateNotEmpty(String str) throws IllegalArgumentException{
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("This name or category cannot be empty");
        }
    }

    private void validateSkillLevel(int level) throws IllegalArgumentException {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5.");
        }
    }
    public void deleteSkillById(int skillId) {
        if (skillId <= 0) throw new IllegalArgumentException("Invalid skill ID.");

        boolean deleted = skillRepo.deleteById(skillId);
        if (!deleted) {
            throw new IllegalArgumentException("Skill with ID " + skillId + " not found.");
        }
    }
}
