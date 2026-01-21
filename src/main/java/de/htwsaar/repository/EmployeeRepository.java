package de.htwsaar.repository;

import de.htwsaar.domain.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository {

    private final Connection conn;

    public EmployeeRepository(Connection conn) {
        this.conn = conn;
    }

    /**
     * Speichert einen Employee als Eintrag in der Datenbank.
     */
    public Employee save(String firstName, String lastName,
                         String email, String role, String status,
                         String hireDate, int workingHours) {

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO employee(first_Name, last_Name, mail, role, status, hire_date, working_hours) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, role);
            stmt.setString(5, status);
            stmt.setString(6, hireDate);
            stmt.setInt(7, workingHours);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No generated key returned for employee insert.");
                }
                return new Employee(keys.getInt(1), firstName, lastName, email, role, status, hireDate, workingHours);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while saving employee", e);
        }
    }

    public Optional<Employee> findByID(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employee WHERE id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                return Optional.of(new Employee(
                        rs.getInt("id"),
                        rs.getString("first_Name"),
                        rs.getString("last_Name"),
                        rs.getString("mail"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("hire_date"),
                        rs.getInt("working_hours")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while finding employee by id", e);
        }
    }

    public List<Employee> findByLastName(String lastName) {
        List<Employee> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM employee WHERE last_Name LIKE ? COLLATE NOCASE")) {

            stmt.setString(1, "%" + lastName.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Employee(
                            rs.getInt("id"),
                            rs.getString("first_Name"),
                            rs.getString("last_Name"),
                            rs.getString("mail"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getString("hire_date"),
                            rs.getInt("working_hours")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while searching employees by last name", e);
        }

        return result;
    }

    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employee")) {

            while (rs.next()) {
                result.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("first_Name"),
                        rs.getString("last_Name"),
                        rs.getString("mail"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("hire_date"),
                        rs.getInt("working_hours")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while loading all employees", e);
        }
        return result;
    }

    public boolean existsByEmail(String email) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM employee WHERE mail = ? LIMIT 1")) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while checking email existence", e);
        }
    }

    public boolean existsByEmailForOtherEmployee(String email, int employeeId) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT 1 FROM employee WHERE mail = ? AND id <> ? LIMIT 1")) {

            stmt.setString(1, email);
            stmt.setInt(2, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while checking email existence for other employee", e);
        }
    }

    public void update(Employee emp) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE employee SET first_Name = ?, last_Name = ?, mail = ?, role = ?, status = ?, hire_date = ?, working_hours = ? WHERE id = ?")) {

            stmt.setString(1, emp.getFirstName());
            stmt.setString(2, emp.getLastName());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getRole());
            stmt.setString(5, emp.getStatus());
            stmt.setString(6, emp.getHireDate());
            stmt.setInt(7, emp.getWorkingHours());
            stmt.setInt(8, emp.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while updating employee", e);
        }
    }
    public boolean deleteById(int employeeId) {
        try {
            conn.setAutoCommit(false);

            // Skills-Zuordnungen löschen (Join-Tabelle)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM employee_skill WHERE employee_id = ?")) {
                stmt.setInt(1, employeeId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {
                // Falls eure Join-Tabelle anders heißt, werfen wir gleich unten die richtige Exception
            }

            // Certificates-Zuordnungen löschen (Join-Tabelle)
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM employee_certificate WHERE employee_id = ?")) {
                stmt.setInt(1, employeeId);
                stmt.executeUpdate();
            } catch (SQLException ignored) {
            }

            // Mitarbeiter löschen
            int affected;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM employee WHERE id = ?")) {
                stmt.setInt(1, employeeId);
                affected = stmt.executeUpdate();
            }

            conn.commit();
            return affected > 0;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Database error occurred while deleting employee: " + e.getMessage(), e);

        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
}