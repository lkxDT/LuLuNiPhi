package de.htwsaar.repository;

import de.htwsaar.domain.Employee;
import de.htwsaar.util.DateFormatChecker;

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
     * Speichert einen Skill als Eintrag in der Datenbank
     * @param firstName Firstname of employee
     * @param lastName Lastname of employee
     * @param email Email of employee
     * @param role Role of employee
     * @param status If the employee is currently in active employment, on leave, not available or not there anymore
     * @param hireDate Date of employment
     * @param workingHours Working hours per week
     * @return Domain object of employee
     */
    public Employee save(String firstName, String lastName,
                  String email, String role, String status,
                  String hireDate, int workingHours) {
        //
        try(PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO employee(first_Name,last_Name, mail, role, status, hire_date, working_hours) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            //Assignment Logic
            if(!email.contains("@") ||
                    !DateFormatChecker.check(hireDate) ||
                    workingHours < 0 ||
                    workingHours > 168) {
                throw new IllegalArgumentException("invalid data");
            }

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3,email);
            stmt.setString(4, role);
            stmt.setString(5, status);
            stmt.setString(6, hireDate);
            stmt.setInt(7, workingHours);

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            return new Employee(keys.getInt(1), firstName, lastName, email, role, status, hireDate, workingHours);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Employee> findByID (int id) {
        try (PreparedStatement stmt = conn.prepareStatement("select * from employee where id = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                return Optional.empty();
            }

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
        } catch (SQLException e) {
            throw new RuntimeException("Database error occured");

        }
    }

    /**
     *
     * @return List of all employees
     */
    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
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
                        rs.getInt("working_hours")));

                            //..

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
