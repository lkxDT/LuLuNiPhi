package de.htwsaar.repository;

import de.htwsaar.domain.AssignedCertificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCertificateRepository {

    private final Connection conn;

    public EmployeeCertificateRepository(Connection conn) {
        this.conn = conn;
    }

    public void assign(int employeeId,
                       int certificateId,
                       String validFrom,
                       String validUntil,
                       String certificateNumber,
                       String status) {

        String sql =
                "INSERT INTO employee_certificate " +
                        "(employee_id, certificate_id, valid_from, valid_until, certificate_number, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.setInt(2, certificateId);
            stmt.setString(3, validFrom);
            stmt.setString(4, validUntil);          // darf null sein
            stmt.setString(5, certificateNumber);   // darf null sein
            stmt.setString(6, status);

            stmt.executeUpdate();

        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            // UNIQUE/PK verletzt -> schon zugewiesen
            if (msg.contains("unique") || msg.contains("constraint")) {
                throw new IllegalStateException("This certificate is already assigned to the employee.");
            }
            throw new RuntimeException("Database error occurred while assigning certificate: " + e.getMessage(), e);
        }
    }

    public List<AssignedCertificate> findByEmployeeId(int employeeId) {
        String sql =
                "SELECT " +
                        "  c.id AS certificate_id, c.name, c.category, c.issuer_type, " +
                        "  ec.valid_from, ec.valid_until, ec.certificate_number, ec.status " +
                        "FROM employee_certificate ec " +
                        "JOIN certificate c ON c.id = ec.certificate_id " +
                        "WHERE ec.employee_id = ? " +
                        "ORDER BY c.name COLLATE NOCASE";


        List<AssignedCertificate> result = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new AssignedCertificate(
                            rs.getInt("certificate_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("issuer_type"),
                            rs.getString("valid_from"),
                            rs.getString("valid_until"),
                            rs.getString("certificate_number"),
                            rs.getString("status")
                    ));
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred while loading employee certificates: " + e.getMessage(), e);
        }
    }
    public boolean existsAssignment(int employeeId, int certificateId) {
        String sql = "SELECT 1 FROM employee_certificate WHERE employee_id = ? AND certificate_id = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, certificateId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while checking certificate assignment", e);
        }
    }
    public boolean isNotAssigned(int employeeId, int certificateId) {
        return !existsAssignment(employeeId, certificateId);
    }

    public void updateAssignment(int employeeId,
                                 int certificateId,
                                 String validFrom,
                                 String validUntil,
                                 String certificateNumber,
                                 String status) {
        String sql =
                "UPDATE employee_certificate SET valid_from = ?, valid_until = ?, certificate_number = ?, status = ? " +
                        "WHERE employee_id = ? AND certificate_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, validFrom);
            stmt.setString(2, validUntil);
            stmt.setString(3, certificateNumber);
            stmt.setString(4, status);
            stmt.setInt(5, employeeId);
            stmt.setInt(6, certificateId);

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                throw new IllegalStateException("No assignment found for this employee and certificate.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating certificate assignment", e);
        }
    }

    public void removeAssignment(int employeeId, int certificateId) {
        String sql = "DELETE FROM employee_certificate WHERE employee_id = ? AND certificate_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            stmt.setInt(2, certificateId);

            int deleted = stmt.executeUpdate();
            if (deleted == 0) {
                throw new IllegalStateException("No assignment found for this employee and certificate.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while removing certificate assignment", e);
        }
    }
}