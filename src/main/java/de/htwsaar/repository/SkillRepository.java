package de.htwsaar.repository;

import de.htwsaar.domain.Employee;
import de.htwsaar.domain.Skill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
                             "INSERT INTO skill(name, category) VALUES (?, ?)",
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
}
