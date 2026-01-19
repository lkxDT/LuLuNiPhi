package de.htwsaar.repository;

import de.htwsaar.domain.Skill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkillRepository {

    private final Connection conn;

    public SkillRepository(Connection conn) {
        this.conn = conn;
    }

    /**
     * Speichert einen Skill als Eintrag in der Datenbank
     * @param name Name des übergebenen Skills
     * @param category Name der übergebenen Kategorie
     * @return DomainObjekt
     */
    public Skill save(String name, String category) {
        try (PreparedStatement stmt =
                     conn.prepareStatement(
                             "INSERT INTO skill(skill_name, category) VALUES (?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            return new Skill(keys.getInt(1), name, category);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Skill> findByID(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from skill where id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(new Skill(
                    rs.getInt("id"),
                    rs.getString("skill_name"),
                    rs.getString("category")

            ));
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred");

        }
    }

    public Optional<Skill> findByName(String name) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from skill where skill_name = ?")) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(new Skill(
                    rs.getInt("id"),
                    rs.getString("skill_name"),
                    rs.getString("category")

            ));
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred");

        }
    }

    public List<Skill> findAll() {
        List<Skill> result = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM skill")) {
            while (rs.next()) {
                result.add(new Skill(
                        rs.getInt("id"),
                        rs.getString("skill_name"),
                        rs.getString("category")
                ));

                //..

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void update(Skill skill) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE skill SET skill_name = ?, category = ? WHERE id = ?")) {

            stmt.setString(1, skill.getName());
            stmt.setString(2, skill.getCategory());
            stmt.setInt(3, skill.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        }
    }
}
