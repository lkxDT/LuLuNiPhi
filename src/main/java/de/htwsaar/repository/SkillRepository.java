package de.htwsaar.repository;

import de.htwsaar.domain.Skill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class SkillRepository {

    private final Connection conn;

    public SkillRepository(Connection conn) {
        this.conn = conn;
    }

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
}
