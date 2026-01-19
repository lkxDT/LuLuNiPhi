package de.htwsaar.applicationtests;

import de.htwsaar.application.SkillService;
import de.htwsaar.domain.Skill;
import de.htwsaar.repositoryTest.MockEmployeeSkillRepository;
import de.htwsaar.repositoryTest.MockSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SkillServiceTest {

    private SkillService skillService;
    private MockSkillRepository mockRepo;
    private MockEmployeeSkillRepository mockEmployeeSkillRepo;

    @BeforeEach
    void setUp() {
        this.mockRepo = new MockSkillRepository();
        this.mockEmployeeSkillRepo = new MockEmployeeSkillRepository();
        this.skillService = new SkillService(mockRepo, null);
    }


                                    //FUNCTIONALITY TESTS

    @Test
    @DisplayName("findAllSkills: Should return the full list from the repository")
    void testFindAllSkills() {
        List<Skill> skills = skillService.findAllSkills();

        assertNotNull(skills);
        assertEquals(2, skills.size(), "MockRepo should start with 2 skills (Java, SQL)");
        System.err.println("\u001B[32m✔ SkillService: findAllSkills implementation verified\u001B[0m");
    }

    @Test
    @DisplayName("findSkillByName: Should find an existing skill")
    void testFindSkillByName() {
        Optional<Skill> result = skillService.findSkillByName("Java");

        assertTrue(result.isPresent(), "Skill 'Java' should be found");
        assertEquals("Java", result.get().getName());
        System.err.println("\u001B[32m✔ SkillService: findSkillByName correctly retrieved 'Java'\u001B[0m");
    }

    @Test
    @DisplayName("createSkill: Should add a new skill with incremental ID")
    void testCreateSkill() {
        Skill newSkill = skillService.createSkill("Python", "Scripting Language");
        assertNotNull(newSkill);
        assertEquals(3, newSkill.getId(), "Next ID should be 3");
        assertEquals("Python", newSkill.getName());
        System.err.println("\u001B[32m✔ SkillService: Correctly created new skill 'Python' with ID 3\u001B[0m");
    }

    // =========================================================================
    //         DATA VALIDATION (Edge Cases & Error Handling)
    // =========================================================================

    @Test
    @DisplayName("Validation: Should not allow empty or null names")
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> skillService.createSkill("", "Category"));
        assertThrows(IllegalArgumentException.class, () -> skillService.createSkill(null, "Category"));
        System.err.println("\u001B[32m✔ SkillService: Successfully blocked invalid name inputs\u001B[0m");
    }


    @Test
    @DisplayName("Validation: Should not allow duplicate skill names")
    void testCreateDuplicateSkill() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill("Java", "Duplicate");
        }, "\u001B[31m[LOGIC ERROR] SkillService allowed a duplicate name! must check if the skill exists before saving.\u001B[0m");
        System.err.println("\u001B[32m✔ SkillService: Correctly blocked duplicate skill creation.\u001B[0m");
    }

    @Test
    @DisplayName("Assignment: Should validate level range (1-5)")
    void testInvalidSkillLevel() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.assignSkill(1, 1, 6, "2026-01-19");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            skillService.assignSkill(1, 1, 0, "2026-01-19");
        });

        System.err.println("\u001B[32m✔ SkillService: Successfully validated skill level boundaries\u001B[0m");
    }
}
