package de.htwsaar.repository;

import de.htwsaar.domain.Certificate;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static de.htwsaar.jooq.tables.Certificate.CERTIFICATE;
import static de.htwsaar.util.JooqContext.using;

public class CertificateRepository {

    private final DSLContext dsl;

    public CertificateRepository(Connection conn) {
        this.dsl = using(conn);
    }

    // --- CREATE ---
    public Certificate save(String name,
                            String category,
                            String issuerType,
                            boolean requiresRenewal,
                            Integer renewalCycleMonths,
                            String description) {
        try {
            Integer newId = dsl.insertInto(CERTIFICATE)
                    .set(CERTIFICATE.NAME, name)
                    .set(CERTIFICATE.CATEGORY, category)
                    .set(CERTIFICATE.ISSUER_TYPE, issuerType)
                    .set(CERTIFICATE.REQUIRES_RENEWAL, requiresRenewal ? 1 : 0)
                    .set(CERTIFICATE.RENEWAL_CYCLE_MONTHS, renewalCycleMonths) // may be null
                    .set(CERTIFICATE.DESCRIPTION, description) // may be null
                    .returning(CERTIFICATE.ID)
                    .fetchOne(CERTIFICATE.ID);

            if (newId == null) {
                throw new IllegalStateException("No generated key returned for certificate insert.");
            }

            return new Certificate(newId, name, category, issuerType, requiresRenewal, renewalCycleMonths, description);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while saving certificate", e);
        }
    }

    // --- READ ---
    public Optional<Certificate> findById(int id) {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .where(CERTIFICATE.ID.eq(id))
                    .fetchOptional(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding certificate by id", e);
        }
    }

    public Optional<Certificate> findByName(String name) {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .where(CERTIFICATE.NAME.eq(name))
                    .fetchOptional(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding certificate by name", e);
        }
    }
    public boolean existsByNameAndIssuer(String name, String issuerType) {
        try {
            return dsl.fetchExists(
                    dsl.selectOne()
                            .from(CERTIFICATE)
                            .where(CERTIFICATE.NAME.eq(name))
                            .and(CERTIFICATE.ISSUER_TYPE.eq(issuerType))
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while checking certificate existence", e);
        }
    }

    public List<Certificate> findAll() {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .orderBy(CERTIFICATE.ID.asc())
                    .fetch(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all certificates", e);
        }
    }

    // --- UPDATE ---
    public void update(Certificate cert) {
        try {
            dsl.update(CERTIFICATE)
                    .set(CERTIFICATE.NAME, cert.getName())
                    .set(CERTIFICATE.CATEGORY, cert.getCategory())
                    .set(CERTIFICATE.ISSUER_TYPE, cert.getIssuerType())
                    .set(CERTIFICATE.REQUIRES_RENEWAL, cert.isRequiresRenewal() ? 1 : 0)
                    .set(CERTIFICATE.RENEWAL_CYCLE_MONTHS, cert.getRenewalCycleMonths())
                    .set(CERTIFICATE.DESCRIPTION, cert.getDescription())
                    .where(CERTIFICATE.ID.eq(cert.getId()))
                    .execute();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while updating certificate", e);
        }
    }

    // --- DELETE ---
    public boolean deleteById(int id) {
        try {
            int deleted = dsl.deleteFrom(CERTIFICATE)
                    .where(CERTIFICATE.ID.eq(id))
                    .execute();
            return deleted > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting certificate", e);
        }
    }

    // --- Mapper ---
    private Certificate mapCertificate(Record r) {
        Integer rr = r.get(CERTIFICATE.REQUIRES_RENEWAL);
        boolean requiresRenewal = rr != null && rr == 1;

        return new Certificate(
                r.get(CERTIFICATE.ID),
                r.get(CERTIFICATE.NAME),
                r.get(CERTIFICATE.CATEGORY),
                r.get(CERTIFICATE.ISSUER_TYPE),
                requiresRenewal,
                r.get(CERTIFICATE.RENEWAL_CYCLE_MONTHS),
                r.get(CERTIFICATE.DESCRIPTION)
        );
    }
}