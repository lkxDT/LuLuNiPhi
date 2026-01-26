package de.htwsaar.domainTest;

import de.htwsaar.domain.AssignedSkill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssignedSkillTest {

    @Test
    @DisplayName("AssignedSkill: Constructor and Getters should correctly assign all values")
    void testAssignedSkillDataIntegrity() {
        AssignedSkill assigned = new AssignedSkill(
                101, "Java", "Programming", 4, "2026-01-20"
        );

        assertAll("Verify all assigned skill fields",
                () -> assertEquals(101, assigned.getId()),
                () -> assertEquals("Java", assigned.getName()),
                () -> assertEquals("Programming", assigned.getCategory()),
                () -> assertEquals(4, assigned.getLevel()),
                () -> assertEquals("2026-01-20", assigned.getSinceDate())
        );

        System.err.println("\u001B[32m✔ AssignedSkill: Data integrity verified with single test case\u001B[0m");
    }
}
