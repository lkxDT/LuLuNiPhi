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

public class SkillRepository {

    private final DSLContext dsl;

    public SkillRepository(Connection conn) {
        this.dsl = using(conn);
    }

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

    public Optional<Skill> findByID(int id) {
        try {
            return dsl.selectFrom(SKILL)
                    .where(SKILL.ID.eq(id))
                    .fetchOptional(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding skill by id", e);
        }
    }

    public Optional<Skill> findByName(String name) {
        try {
            return dsl.selectFrom(SKILL)
                    .where(SKILL.SKILL_NAME.eq(name))
                    .fetchOptional(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding skill by name", e);
        }
    }

    public List<Skill> findAll() {
        try {
            return dsl.selectFrom(SKILL)
                    .orderBy(SKILL.ID.asc())
                    .fetch(this::mapSkill);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all skills", e);
        }
    }

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

    private Skill mapSkill(Record r) {
        return new Skill(
                r.get(SKILL.ID),
                r.get(SKILL.SKILL_NAME),
                r.get(SKILL.CATEGORY)
        );
    }
}