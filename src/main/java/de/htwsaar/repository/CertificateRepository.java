package de.htwsaar.repository;

import de.htwsaar.domain.Certificate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificateRepository {

    private final Connection conn;

    public CertificateRepository(Connection conn) {
        this.conn = conn;
    }

    public List<Certificate> findAll() {
        List<Certificate> result = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM certificate ORDER BY id")) {

            while (rs.next()) {
                Integer renewalMonths = (Integer) rs.getObject("renewal_cycle_months"); // NULL-safe
                String description = rs.getString("description"); // kann NULL sein

                result.add(new Certificate(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("issuer_type"),
                        rs.getInt("requires_renewal") == 1,
                        renewalMonths,
                        description
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occured", e);
        }

        return result;
    }
    public Certificate save(String name,
                            String category,
                            String issuerType,
                            boolean requiresRenewal,
                            Integer renewalCycleMonths,
                            String description) {

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO certificate(name, category, issuer_type, requires_renewal, renewal_cycle_months, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, issuerType);
            stmt.setInt(4, requiresRenewal ? 1 : 0);

            if (renewalCycleMonths == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, renewalCycleMonths);
            }

            if (description == null || description.isBlank()) {
                stmt.setNull(6, Types.VARCHAR);
            } else {
                stmt.setString(6, description);
            }

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);
                return new Certificate(id, name, category, issuerType, requiresRenewal, renewalCycleMonths, description);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error occured", e);
        }
    }
}