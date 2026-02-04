package de.htwsaar.repository;

import de.htwsaar.domain.Skill;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static de.htwsaar.jooq.tables.Skill.SKILL;
import static de.htwsaar.util.JooqContext.using;

/**
 * Repository for managing {@link Skill} persistence using jOOQ.
 * <p>
 * Provides CRUD operations and search functionality for skills.
 */
public class SkillRepository {

    private final DSLContext dsl;

    /**
     * Creates a repository using a JDBC connection.
     *
     * @param conn database connection
     */
    public SkillRepository(Connection conn) {
        this.dsl = using(conn);
    }

    /**
     * Saves a new skill in the database.
     *
     * @param name     skill name
     * @param category skill category
     * @return the created Skill including generated ID
     * @throws RuntimeException if a database error occurs
     */
    public Skill save(String name, String category) {
        try {
            Integer newId = dsl.insertInto(SKILL)
                    .set(SKILL.SKILL_NAME, name)
                    .set(SKILL.CATEGORY, category)
                    .returning(SKILL.ID)
                    .fetchOne(SKILL.ID);

            if (newId == null) {
                throw new IllegalStateException("No generated key returned for skill insert.");
            }

            return new Skill(newId, name, category);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while saving skill", e);
        }
    }

    /**
     * Finds a skill by its ID.
     *
     * @param id skill ID
     * @return an Optional containing the skill if found, otherwise empty
     * @throws RuntimeException if a database error occurs
     */
    public Optional<Skill> findByID(int id) {
        try {
            return dsl.selectFrom(SKILL)
                    .where(SKILL.ID.eq(id))
                    .fetchOptional(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding skill by id", e);
        }
    }

    /**
     * Finds a skill by its exact name.
     *
     * @param name skill name
     * @return an Optional containing the skill if found, otherwise empty
     * @throws RuntimeException if a database error occurs
     */
    public Optional<Skill> findByName(String name) {
        try {
            return dsl.selectFrom(SKILL)
                    .where(SKILL.SKILL_NAME.eq(name))
                    .fetchOptional(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding skill by name", e);
        }
    }

    /**
     * Loads all skills ordered by ID.
     *
     * @return list of all skills
     * @throws RuntimeException if a database error occurs
     */
    public List<Skill> findAll() {
        try {
            return dsl.selectFrom(SKILL)
                    .orderBy(SKILL.ID.asc())
                    .fetch(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all skills", e);
        }
    }

    /**
     * Updates an existing skill in the database.
     *
     * @param skill skill with updated values
     * @throws RuntimeException if a database error occurs
     */
    public void update(Skill skill) {
        try {
            dsl.update(SKILL)
                    .set(SKILL.SKILL_NAME, skill.getName())
                    .set(SKILL.CATEGORY, skill.getCategory())
                    .where(SKILL.ID.eq(skill.getId()))
                    .execute();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while updating skill", e);
        }
    }

    /**
     * Deletes a skill by its ID.
     *
     * @param id skill ID
     * @return true if the skill was deleted, otherwise false
     * @throws RuntimeException if a database error occurs
     */
    public boolean deleteById(int id) {
        try {
            int deleted = dsl.deleteFrom(SKILL)
                    .where(SKILL.ID.eq(id))
                    .execute();

            return deleted > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting skill", e);
        }
    }

    /**
     * Maps a jOOQ {@link Record} to a {@link Skill}.
     *
     * @param r database record
     * @return mapped Skill instance
     */
    private Skill mapSkill(Record r) {
        return new Skill(
                r.get(SKILL.ID),
                r.get(SKILL.SKILL_NAME),
                r.get(SKILL.CATEGORY)
        );
    }

    /**
     * Searches skills by name using a case-insensitive LIKE query.
     *
     * @param name skill name or partial name (nullable)
     * @return list of matching skills
     */
    public List<Skill> findSkillsByName(String name) {
        String q = (name == null) ? "" : name.trim();

        return dsl.selectFrom(SKILL)
                .where(SKILL.SKILL_NAME.likeIgnoreCase("%" + q + "%"))
                .orderBy(SKILL.ID.asc())
                .fetch(this::mapSkill);
    }
}