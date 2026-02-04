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

/**
 * Repository for managing {@link Certificate} persistence using jOOQ.
 * <p>
 * Provides CRUD operations and mapping between database records
 * and {@link Certificate} domain objects.
 */
public class CertificateRepository {

    private final DSLContext dsl;

    /**
     * Creates a repository using a JDBC connection.
     *
     * @param conn database connection
     */
    public CertificateRepository(Connection conn) {
        this.dsl = using(conn);
    }

    /**
     * Creates a repository using an existing jOOQ {@link DSLContext}.
     *
     * @param dsl jOOQ DSL context
     */
    public CertificateRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Returns the jOOQ context used by this repository.
     *
     * @return DSLContext instance
     */
    public DSLContext ctx() {
        return dsl;
    }

    /**
     * Saves a new certificate in the database.
     *
     * @param name               certificate name
     * @param category           certificate category
     * @param issuerType         issuing authority
     * @param requiresRenewal    whether renewal is required
     * @param renewalCycleMonths renewal cycle in months (nullable)
     * @param description        optional description (nullable)
     * @return the created Certificate including generated ID
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Finds a certificate by its ID.
     *
     * @param id certificate ID
     * @return an Optional containing the certificate if found, otherwise empty
     * @throws RuntimeException if a database error occurs
     */
    public Optional<Certificate> findById(int id) {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .where(CERTIFICATE.ID.eq(id))
                    .fetchOptional(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding certificate by id", e);
        }
    }

    /**
     * Finds a certificate by its name.
     *
     * @param name certificate name
     * @return an Optional containing the certificate if found, otherwise empty
     * @throws RuntimeException if a database error occurs
     */
    public Optional<Certificate> findByName(String name) {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .where(CERTIFICATE.NAME.eq(name))
                    .fetchOptional(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while finding certificate by name", e);
        }
    }
    /**
     * Checks whether a certificate with the given name and issuer exists.
     *
     * @param name       certificate name
     * @param issuerType issuer type
     * @return true if such a certificate exists
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Loads all certificates ordered by ID.
     *
     * @return list of all certificates
     * @throws RuntimeException if a database error occurs
     */
    public List<Certificate> findAll() {
        try {
            return dsl.selectFrom(CERTIFICATE)
                    .orderBy(CERTIFICATE.ID.asc())
                    .fetch(this::mapCertificate);
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading all certificates", e);
        }
    }

    /**
     * Updates an existing certificate in the database.
     *
     * @param cert certificate with updated values
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Deletes a certificate by its ID.
     *
     * @param id certificate ID
     * @return true if a row was deleted, otherwise false
     * @throws RuntimeException if a database error occurs
     */
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

    /**
     * Maps a jOOQ {@link Record} to a {@link Certificate}.
     *
     * @param r database record
     * @return mapped Certificate instance
     */
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