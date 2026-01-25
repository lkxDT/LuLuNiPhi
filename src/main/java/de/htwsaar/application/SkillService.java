package de.htwsaar.application;

import java.util.List;
import java.util.Optional;
import de.htwsaar.domain.AssignedSkill;
import de.htwsaar.domain.Skill;
import de.htwsaar.repository.EmployeeSkillRepository;
import de.htwsaar.repository.SkillRepository;

public class SkillService {

    private final SkillRepository skillRepo;
    private final EmployeeSkillRepository employeeSkillRepo;

    /**
     * Creates a new SkillService instance.
     *
     * @param s  SkillRepository used for skill persistence
     * @param es EmployeeSkillRepository used for employee-skill assignments
     */
    public SkillService(SkillRepository s, EmployeeSkillRepository es) {
        this.skillRepo = s;
        this.employeeSkillRepo = es;
    }

    /**
     * Retrieves all skills from the repository.
     *
     * @return a list of all skills
     */
    public List<Skill> findAllSkills() {
        return skillRepo.findAll();
    }

    /**
     * Finds a skill by its ID.
     *
     * @param id the skill's ID
     * @return an Optional containing the skill if found, or empty if not
     */
    public Optional<Skill> findSkillByID(int id) {
        return skillRepo.findByID(id);
    }

    /**
     * Finds a skill by its name.
     *
     * @param name the name of the skill
     * @return an Optional containing the skill if found, or empty if not
     */
    public Optional<Skill> findSkillByName(String name) {
        return skillRepo.findByName(name);
    }

    /**
     * Assigns a skill to an employee.
     *
     * @param empId     ID of the employee
     * @param skillId   ID of the skill to assign
     * @param level     Skill level (1-5)
     * @param sinceDate Date since the skill is assigned
     * @throws IllegalStateException    if the skill is already assigned to the employee
     * @throws IllegalArgumentException if the skill level is invalid
     */
    public void assignSkill(int empId, int skillId, int level, String sinceDate) {
        validateSkillLevel(level);
        if (employeeSkillRepo.existsAssignment(empId, skillId)) {
            throw new IllegalStateException(
                    "This skill is already assigned to the employee."
            );
        }

        employeeSkillRepo.assign(empId, skillId, level, sinceDate);
    }

    /**
     * Updates an existing skill assignment for an employee.
     *
     * @param empId     ID of the employee
     * @param skillId   ID of the skill
     * @param level     Skill level (1-5)
     * @param sinceDate Date since the skill is assigned
     * @throws IllegalArgumentException if the level is out of range or sinceDate is empty
     */
    public void updateSkillAssignment(int empId, int skillId, int level, String sinceDate) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5.");
        }
        if (sinceDate == null || sinceDate.isBlank()) {
            throw new IllegalArgumentException("Since date must not be empty.");
        }

        employeeSkillRepo.updateAssignment(empId, skillId, level, sinceDate);
    }

    /**
     * Removes a skill assignment from an employee.
     *
     * @param empId   ID of the employee
     * @param skillId ID of the skill to remove
     * @throws IllegalStateException if the skill is not assigned to the employee
     */
    public void removeSkillFromEmployee(int empId, int skillId) {

        if (!employeeSkillRepo.existsAssignment(empId, skillId)) {
            throw new IllegalStateException(
                    "This skill is not assigned to the employee."
            );
        }

        employeeSkillRepo.removeAssignment(empId, skillId);
    }

    /**
     * Creates and saves a new skill.
     *
     * @param name     Name of the skill
     * @param category Category of the skill
     * @return the created Skill
     * @throws IllegalArgumentException if name or category is empty, or if the skill already exists
     */
    public Skill createSkill(String name, String category) {
        validateNotEmpty(name);
        validateNotEmpty(category);
        if(skillRepo.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Skill already exists");
        }
        return skillRepo.save(name.trim(), category.trim());
    }

    /**
     * Updates an existing skill.
     *
     * @param skill the Skill object with updated data
     * @throws IllegalArgumentException if the skill name or category is empty
     */
    public void updateSkill(Skill skill) {
        validateNotEmpty(skill.getName());
        validateNotEmpty(skill.getCategory());
        skillRepo.update(skill);
    }

    /**
     * Retrieves all skills assigned to a specific employee.
     *
     * @param empId ID of the employee
     * @return a list of AssignedSkill objects for the employee
     */
    public List<AssignedSkill> findSkillsOfEmployee(int empId) {
        return employeeSkillRepo.findSkillsByEmployeeId(empId);
    }

    /**
     * Validates that a string is not null or blank.
     *
     * @param str the string to validate
     * @throws IllegalArgumentException if the string is null or blank
     */
    private void validateNotEmpty(String str) throws IllegalArgumentException{
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("This name or category cannot be empty");
        }
    }

    /**
     * Validates that a skill level is within the allowed range (1-5).
     *
     * @param level the skill level to validate
     * @throws IllegalArgumentException if the level is not between 1 and 5
     */
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
