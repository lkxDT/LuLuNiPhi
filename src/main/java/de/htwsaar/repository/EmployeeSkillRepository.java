package de.htwsaar.repository;

import de.htwsaar.domain.AssignedSkill;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;

import static de.htwsaar.jooq.tables.EmployeeSkill.EMPLOYEE_SKILL;
import static de.htwsaar.jooq.tables.Skill.SKILL;
import static de.htwsaar.util.JooqContext.using;

public class EmployeeSkillRepository {

    private final DSLContext dsl;

    public EmployeeSkillRepository(Connection conn) {
        this.dsl = using(conn);
    }

    // --- ASSIGN ---
    public void assign(int employeeId, int skillId, int level) {
        try {
            dsl.insertInto(EMPLOYEE_SKILL)
                    .set(EMPLOYEE_SKILL.EMPLOYEE_ID, employeeId)
                    .set(EMPLOYEE_SKILL.SKILL_ID, skillId)
                    .set(EMPLOYEE_SKILL.LEVEL, level)
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

    // --- EXISTS ---
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

    public boolean isNotAssigned(int employeeId, int skillId) {
        return !existsAssignment(employeeId, skillId);
    }

    // --- FIND BY EMPLOYEE ---
    public List<AssignedSkill> findByEmployeeId(int employeeId) {
        try {
            return dsl.select(
                            SKILL.ID.as("skill_id"),
                            SKILL.SKILL_NAME,
                            SKILL.CATEGORY,
                            EMPLOYEE_SKILL.LEVEL
                    )
                    .from(EMPLOYEE_SKILL)
                    .join(SKILL).on(SKILL.ID.eq(EMPLOYEE_SKILL.SKILL_ID))
                    .where(EMPLOYEE_SKILL.EMPLOYEE_ID.eq(employeeId))
                    .orderBy(SKILL.SKILL_NAME.asc())
                    .fetch(this::mapAssignedSkill);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading employee skills", e);
        }
    }
    public List<AssignedSkill> findSkillsByEmployeeId(int employeeId) {
        return findByEmployeeId(employeeId);
    }

    // --- UPDATE ---
    public void updateAssignment(int employeeId, int skillId, int level) {
        try {
            int updated = dsl.update(EMPLOYEE_SKILL)
                    .set(EMPLOYEE_SKILL.LEVEL, level)
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
    public void updateAssignment(int employeeId, int skillId, int level, String levelName) {
        // levelName is derived information, not stored in DB
        updateAssignment(employeeId, skillId, level);
    }

    // --- REMOVE ---
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

    // --- MAPPER ---
    private AssignedSkill mapAssignedSkill(Record r) {
        int level = r.get(EMPLOYEE_SKILL.LEVEL);

        String levelName = switch (level) {
            case 1 -> "Beginner";
            case 2 -> "Intermediate";
            case 3 -> "Advanced";
            case 4 -> "Expert";
            default -> "Unknown";
        };

        return new AssignedSkill(
                r.get("skill_id", Integer.class),
                r.get(SKILL.SKILL_NAME),
                r.get(SKILL.CATEGORY),
                level,
                levelName
        );
    }
    public void assign(int employeeId, int skillId, int level, String levelName) {
        // levelName is derived information, not stored in DB
        assign(employeeId, skillId, level);
    }
}