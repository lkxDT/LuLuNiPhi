package de.htwsaar.domainTest;

import de.htwsaar.domain.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkillTest {

    @Test
    @DisplayName("Skill: Constructor and Getters should correctly assign values")
    void testSkillCreation() {
        Skill skill = new Skill(10, "Java Programming", "Software Development");

        assertAll("Verify all skill fields",
                () -> assertEquals(10, skill.getId(), "ID should be 10"),
                () -> assertEquals("Java Programming", skill.getName(), "Name should match"),
                () -> assertEquals("Software Development", skill.getCategory(), "Category should match")
        );

        System.err.println("\u001B[32m✔ Skill: Creation and data integrity verified\u001B[0m");
    }

    @Test
    @DisplayName("Skill: toString should follow the %4d | %25s | %25s format")
    void testToStringFormatting() {
        Skill skill = new Skill(1, "SQL", "Database");
        String output = skill.toString();

        assertNotNull(output);
        assertTrue(output.startsWith("   1"), "ID formatting (padding) is incorrect in: '" + output + "'");
        assertTrue(output.contains("|"), "Separators missing in toString");
        assertTrue(output.contains("SQL"), "Name missing in toString");
        assertTrue(output.contains("Database"), "Category missing in toString");

        System.err.println("\u001B[32m✔ Skill: toString formatting (padding and separators) verified\u001B[0m");
    }

    @Test
    @DisplayName("Skill: Should handle null or empty values gracefully")
    void testNullValues() {
        Skill skill = new Skill(0, null, "");

        assertNull(skill.getName());
        assertEquals("", skill.getCategory());

        System.err.println("\u001B[32m✔ Skill: Domain object handles null/empty inputs without crash\u001B[0m");
    }
}