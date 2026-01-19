package de.htwsaar.applicationtests;

import de.htwsaar.application.SkillService;
import de.htwsaar.domain.Skill;
import de.htwsaar.repositoryTest.MockSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillServiceTest {

    private SkillService skillService;
    private MockSkillRepository mockRepo;

    @BeforeEach
    void setUp() {
        this.mockRepo = new MockSkillRepository();
        this.skillService = new SkillService(mockRepo, null);
    }

    // =========================================================================
    //                         ACCOUNTABILITY TESTS
    // =========================================================================

    @Test
    @DisplayName("Accountability: Check if getAllSkills is implemented and working")
    void testSkillServiceImplementationStatus() {
        Object result = null;

        try {
            java.lang.reflect.Method method = skillService.getClass().getMethod("findAllSkills");
            result = method.invoke(skillService);
        } catch (NoSuchMethodException e) {
            fail("\u001B[31m[MISSING METHOD] SkillService is missing the 'getAllSkills()' method!.\u001B[0m");
        } catch (Exception e) {
            fail("\u001B[31m[CRASH] The method exists but it crashed when called: " + e.getCause() + "\u001B[0m");
        }

        assertNotNull(result, "\u001B[31m[LOGIC ERROR] getAllSkills() exists but it returns NULL. It should return a List.\u001B[0m");

        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            assertEquals(2, list.size(),
                    "\u001B[31m[INTEGRATION ERROR] getAllSkills() is not returning the Mock data. Expected 2 skills, found " + list.size() + ".\u001B[0m");
            System.err.println("\u001B[32m✔ SkillService: Implementation verified! Method exists and returns data.\u001B[0m");
        } else {
            fail("\u001B[31m[TYPE ERROR] getAllSkills() should return a List, but it returned a " + result.getClass().getSimpleName() + ".\u001B[0m");
        }
    }

    @Test
    @DisplayName("Accountability: Check if findSkillByName is implemented")
    void testFindSkillByNameImplementation() {
        try {
            java.lang.reflect.Method method = skillService.getClass().getMethod("findSkillByName", String.class);
            Object result = method.invoke(skillService, "Java");
            assertNotNull(result, "\u001B[31m[LOGIC ERROR] findSkillByName('Java') returned null but Java exists in Mock.\u001B[0m");
            System.err.println("\u001B[32m✔ SkillService: findSkillByName is implemented and working.\u001B[0m");
        } catch (NoSuchMethodException e) {
            fail("\u001B[31m[MISSING METHOD] SkillService needs a 'findSkillByName(String)' method for search functionality.\u001B[0m");
        } catch (Exception e) {
            fail("\u001B[31m[CRASH] findSkillByName exists but crashed: " + e.getMessage() + "\u001B[0m");
        }
    }

    // =========================================================================
    //                BASIC FUNCTIONALITY (Happy Path)
    // =========================================================================

    @Test
    @DisplayName("createSkill: Should add a new skill with incremental ID")
    void testCreateSkill() {
        Skill newSkill = skillService.createSkill("Python", "Scripting Language");
        assertNotNull(newSkill);
        assertEquals(3, newSkill.getId());
        assertEquals("Python", newSkill.getName());
        System.err.println("\u001B[32m✔ SkillService: Correctly created new skill 'Python' with ID 3\u001B[0m");
    }

    // =========================================================================
    //         DATA VALIDATION (Edge Cases & Error Handling)
    // =========================================================================

    @Test
    @DisplayName("Validation: Should not allow empty skill names")
    void testCreateSkillWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill("", "No name provided");
        }, "\u001B[31m[LOGIC ERROR] SkillService accepted an empty string! must add an isEmpty() check.\u001B[0m");

        System.err.println("\u001B[32m✔ SkillService: Successfully blocked empty skill name validation\u001B[0m");
    }

    @Test
    @DisplayName("Validation: Should throw exception for null skill name")
    void testCreateSkillWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill(null, "Description");
        }, "\u001B[31m[LOGIC ERROR] SkillService accepted a NULL name! must add a null check.\u001B[0m");
        System.err.println("\u001B[32m✔ SkillService: Successfully blocked NULL skill name.\u001B[0m");
    }

    @Test
    @DisplayName("Validation: Should not allow duplicate skill names")
    void testCreateDuplicateSkill() {
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill("Java", "Duplicate");
        }, "\u001B[31m[LOGIC ERROR] SkillService allowed a duplicate name! must check if the skill exists before saving.\u001B[0m");

        System.err.println("\u001B[32m✔ SkillService: Correctly blocked duplicate skill creation.\u001B[0m");
    }
}
