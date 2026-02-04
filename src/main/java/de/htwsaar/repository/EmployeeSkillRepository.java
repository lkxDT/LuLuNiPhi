package de.htwsaar.repository;

import de.htwsaar.domain.AssignedSkill;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;

import static de.htwsaar.jooq.tables.EmployeeSkill.EMPLOYEE_SKILL;
import static de.htwsaar.jooq.tables.Skill.SKILL;
import static de.htwsaar.util.JooqContext.using;

/**
 * Repository for managing skill assignments of employees using jOOQ.
 * <p>
 * Handles persistence of the employee-skill relationship and
 * provides methods to assign, update, remove and query assigned skills.
 */
public class EmployeeSkillRepository {

    private final DSLContext dsl;

    /**
     * Field reference for the "since_date" column.
     * Used as a fallback in case the jOOQ generator did not create a constant.
     */
    private static final Field<String> SINCE_DATE = EMPLOYEE_SKILL.field("since_date", String.class);

    /**
     * Creates a repository using a JDBC connection.
     *
     * @param conn database connection
     */
    public EmployeeSkillRepository(Connection conn) {
        this.dsl = using(conn);
    }

    /**
     * Creates a repository using an existing jOOQ {@link DSLContext}.
     *
     * @param dsl jOOQ DSL context
     */
    public EmployeeSkillRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Returns the jOOQ context used by this repository.
     *
     * @return DSLContext instance
     */
    public DSLContext ctx() {
        return dsl;
    }

    /**
     * Assigns a skill to an employee.
     *
     * @param employeeId employee ID
     * @param skillId    skill ID
     * @param level      proficiency level (1–5)
     * @param sinceDate  date since the skill is assigned
     * @throws IllegalStateException if the assignment already exists or foreign keys are invalid
     * @throws RuntimeException      if a database error occurs
     */
    public void assign(int employeeId, int skillId, int level, String sinceDate) {
        try {
            dsl.insertInto(EMPLOYEE_SKILL)
                    .set(EMPLOYEE_SKILL.EMPLOYEE_ID, employeeId)
                    .set(EMPLOYEE_SKILL.SKILL_ID, skillId)
                    .set(EMPLOYEE_SKILL.LEVEL, level)
                    .set(SINCE_DATE, sinceDate)
                    .execute();
        } catch (DataAccessException e) {
            String msg = (e.getMessage() == null) ? "" : e.getMessage().toLowerCase();

            if (msg.contains("unique constraint failed") || msg.contains("primary key")) {
                throw new IllegalStateException("This skill is already assigned to the employee.", e);
            }

            if (msg.contains("foreign key constraint failed")) {
                throw new IllegalStateException("Invalid employee or skill id (foreign key constraint failed).", e);
            }

            if (msg.contains("not null constraint failed")) {
                throw new IllegalStateException("Missing required data while assigning the skill.", e);
            }

            throw new RuntimeException("Database error occurred while assigning skill: " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether a skill assignment exists for the given employee and skill.
     *
     * @param employeeId employee ID
     * @param skillId    skill ID
     * @return true if the assignment exists
     * @throws RuntimeException if a database error occurs
     */
    public boolean existsAssignment(int employeeId, int skillId) {
        try {
            return dsl.fetchExists(
                    dsl.selectOne()
                            .from(EMPLOYEE_SKILL)
                            .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                            .and(EMPLOYEE_SKILL.SKILL_ID.eq(skillId))
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while checking skill assignment", e);
        }
    }

    /**
     * Loads all skills assigned to a specific employee.
     *
     * @param employeeId employee ID
     * @return list of assigned skills
     * @throws RuntimeException if a database error occurs
     */
    public List<AssignedSkill> findByEmployeeId(int employeeId) {
        try {
            return dsl.select(
                            SKILL.ID.as("skill_id"),
                            SKILL.SKILL_NAME,
                            SKILL.CATEGORY,
                            EMPLOYEE_SKILL.LEVEL,
                            SINCE_DATE
                    )
                    .from(EMPLOYEE_SKILL)
                    .join(SKILL).on(SKILL.ID.eq(EMPLOYEE_SKILL.SKILL_ID))
                    .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                    .orderBy(SKILL.ID.asc())
                    .fetch(this::mapAssignedSkill);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading employee skills", e);
        }
    }

    /**
     * Convenience alias for {@link #findByEmployeeId(int)}.
     *
     * @param employeeId employee ID
     * @return list of assigned skills
     */
    public List<AssignedSkill> findSkillsByEmployeeId(int employeeId) {
        return findByEmployeeId(employeeId);
    }

    /**
     * Updates an existing skill assignment.
     *
     * @param employeeId employee ID
     * @param skillId    skill ID
     * @param level      new proficiency level (1–5)
     * @param sinceDate  new since date
     * @throws IllegalStateException if no matching assignment exists
     * @throws RuntimeException      if a database error occurs
     */
    public void updateAssignment(int employeeId, int skillId, int level, String sinceDate) {
        try {
            int updated = dsl.update(EMPLOYEE_SKILL)
                    .set(EMPLOYEE_SKILL.LEVEL, level)
                    .set(SINCE_DATE, sinceDate)
                    .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                    .and(EMPLOYEE_SKILL.SKILL_ID.eq(skillId))
                    .execute();

            if (updated == 0) {
                throw new IllegalStateException("No assignment found for this employee and skill.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while updating skill assignment", e);
        }
    }

    /**
     * Removes a skill assignment from an employee.
     *
     * @param employeeId employee ID
     * @param skillId    skill ID
     * @throws IllegalStateException if no matching assignment exists
     * @throws RuntimeException      if a database error occurs
     */
    public void removeAssignment(int employeeId, int skillId) {
        try {
            int deleted = dsl.deleteFrom(EMPLOYEE_SKILL)
                    .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                    .and(EMPLOYEE_SKILL.SKILL_ID.eq(skillId))
                    .execute();

            if (deleted == 0) {
                throw new IllegalStateException("No assignment found for this employee and skill.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while removing skill assignment", e);
        }
    }

    /**
     * Maps a jOOQ {@link Record} to an {@link AssignedSkill}.
     *
     * @param r database record
     * @return mapped AssignedSkill instance
     */
    private AssignedSkill mapAssignedSkill(Record r) {
        int level = r.get(EMPLOYEE_SKILL.LEVEL);
        String sinceDate = r.get(SINCE_DATE);

        return new AssignedSkill(
                r.get("skill_id", Integer.class),
                r.get(SKILL.SKILL_NAME),
                r.get(SKILL.CATEGORY),
                level,
                sinceDate
        );
    }
}