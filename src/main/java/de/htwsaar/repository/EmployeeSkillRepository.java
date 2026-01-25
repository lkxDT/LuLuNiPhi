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
            throw new RuntimeException("Database error occurred", e);
        }
    }

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
            throw new RuntimeException("Database error occurred", e);
        }

        return result;
    }

    public boolean existsAssignment(int employeeId, int skillId) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM employee_skill WHERE employee_id = ? AND skill_id = ? LIMIT 1")) {

            stmt.setInt(1, employeeId);
            stmt.setInt(2, skillId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        }
    }
    public boolean isNotAssigned(int employeeId, int skillId) {
        return !existsAssignment(employeeId, skillId);
    }

    public void updateAssignment(int employeeId, int skillId, int level, String sinceDate) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE employee_skill SET level = ?, since_date = ? " +
                        "WHERE employee_id = ? AND skill_id = ?")) {

            stmt.setInt(1, level);
            stmt.setString(2, sinceDate);
            stmt.setInt(3, employeeId);
            stmt.setInt(4, skillId);

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new IllegalStateException("No assignment found for this employee and skill.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        }
    }

    public void removeAssignment(int employeeId, int skillId) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM employee_skill WHERE employee_id = ? AND skill_id = ?")) {

            stmt.setInt(1, employeeId);
            stmt.setInt(2, skillId);

            int deletedRows = stmt.executeUpdate();
            if (deletedRows == 0) {
                throw new IllegalStateException("No assignment found for this employee and skill.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred", e);
        }
    }
}
