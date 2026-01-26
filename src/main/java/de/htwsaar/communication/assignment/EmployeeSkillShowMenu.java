package de.htwsaar.communication.assignment;

import de.htwsaar.application.SkillService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.AssignedSkill;

import java.util.List;

/**
 * Menu for displaying skills assigned to an employee.
 */
public class EmployeeSkillShowMenu {
    /**
     * Utility class constructor.
     * Prevents instantiation.
     */
    private EmployeeSkillShowMenu() {}

    /**
     * Displays the skills assigned to a specific employee.
     *
     * @param out          OutputRenderer for console output
     * @param in           InputReader for user input
     * @param skillService Service for skill operations
     */
    protected static void show(OutputRenderer out, InputReader in, SkillService skillService) {
        System.out.print("Employee ID: ");
        try {
            int empId = in.readInt("Invalid ID.");
            List<AssignedSkill> skills =
                    skillService.findSkillsOfEmployee(empId);
            out.assignedSkillTable(skills);
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
        }
    }
}
