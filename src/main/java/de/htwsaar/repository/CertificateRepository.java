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
}