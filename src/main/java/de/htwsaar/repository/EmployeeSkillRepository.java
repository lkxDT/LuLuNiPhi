package de.htwsaar.repository;

import de.htwsaar.domain.AssignedSkill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeSkillRepository {

    private final Connection conn;

    public EmployeeSkillRepository(Connection conn) {
        this.conn = conn;
    }

    /**
     * Assigns a skill to an employee
     */
    public void assign(int employeeId, int skillId, int level, String sinceDate) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO employee_skill (employee_id, skill_id, level, since_date) " +
                        "VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, employeeId);
            stmt.setInt(2, skillId);
            stmt.setInt(3, level);
            stmt.setString(4, sinceDate);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error occured", e);
        }
    }

    /**
     * Returns all skills assigned to a specific employee
     */
    public List<AssignedSkill> findSkillsByEmployeeId(int employeeId) {
        List<AssignedSkill> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT s.id AS skill_id, s.skill_name, s.category, es.level, es.since_date " +
                        "FROM employee_skill es " +
                        "JOIN skill s ON s.id = es.skill_id " +
                        "WHERE es.employee_id = ? " +
                        "ORDER BY s.skill_name COLLATE NOCASE")) {

            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new AssignedSkill(
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("category"),
                            rs.getInt("level"),
                            rs.getString("since_date")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occured", e);
        }

        return result;
    }
}