package de.htwsaar.repository;

import de.htwsaar.domain.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    private final Connection conn;

    public EmployeeRepository(Connection conn) {
        this.conn = conn;
    }

    public Employee save(String firstName, String lastName,
                  String email, String role, String status,
                  String hireDate, int workingHours) {
        //
        try(PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO employee(first_Name,last_Name, mail, role, status, hire_date, working_hours) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            //Assignment Logic
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

    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("")) {
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
