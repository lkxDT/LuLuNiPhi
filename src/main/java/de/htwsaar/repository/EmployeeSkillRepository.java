package de.htwsaar.repository;
import de.htwsaar.domain.EmployeeSkill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class EmployeeSkillRepository {
    private final Connection conn;

    public EmployeeSkillRepository(Connection conn) {
        this.conn = conn;
    }

    public void assign(int employeeId, int skillId, int level, String sinceDate) {
        try (PreparedStatement stmt =
                     conn.prepareStatement(
                             "INSERT INTO employee_skill VALUES (?,?,?,?)")) {

            stmt.setInt(1, employeeId);
            stmt.setInt(2, skillId);
            stmt.setInt(3, level);
            stmt.setString(4, sinceDate);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
