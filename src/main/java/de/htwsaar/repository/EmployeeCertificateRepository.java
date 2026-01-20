package de.htwsaar.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}