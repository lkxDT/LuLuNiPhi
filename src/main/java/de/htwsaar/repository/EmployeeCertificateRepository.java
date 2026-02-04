package de.htwsaar.repository;

import de.htwsaar.domain.AssignedCertificate;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.util.List;

import static de.htwsaar.jooq.tables.Certificate.CERTIFICATE;
import static de.htwsaar.jooq.tables.EmployeeCertificate.EMPLOYEE_CERTIFICATE;
import static de.htwsaar.util.JooqContext.using;

/**
 * Repository for managing certificate assignments of employees using jOOQ.
 * <p>
 * Handles persistence of the employee-certificate relationship and
 * provides methods to assign, update, remove and query assigned certificates.
 */
public class EmployeeCertificateRepository {

    private final DSLContext dsl;

    /**
     * Creates a repository using a JDBC connection.
     *
     * @param conn database connection
     */
    public EmployeeCertificateRepository(Connection conn) {
        this.dsl = using(conn);
    }

    /**
     * Creates a repository using an existing jOOQ {@link DSLContext}.
     *
     * @param dsl jOOQ DSL context
     */
    public EmployeeCertificateRepository(DSLContext dsl) {
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
     * Assigns a certificate to an employee.
     *
     * @param employeeId        employee ID
     * @param certificateId     certificate ID
     * @param validFrom         validity start date
     * @param validUntil        validity end date (nullable)
     * @param certificateNumber certificate reference number (nullable)
     * @param status            assignment status
     * @throws IllegalStateException if the assignment already exists or foreign keys are invalid
     * @throws RuntimeException      if a database error occurs
     */
    public void assign(int employeeId,
                       int certificateId,
                       String validFrom,
                       String validUntil,
                       String certificateNumber,
                       String status) {

        try {
            dsl.insertInto(EMPLOYEE_CERTIFICATE)
                    .set(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID, employeeId)
                    .set(EMPLOYEE_CERTIFICATE.CERTIFICATE_ID, certificateId)
                    .set(EMPLOYEE_CERTIFICATE.VALID_FROM, validFrom)
                    .set(EMPLOYEE_CERTIFICATE.VALID_UNTIL, validUntil)            // may be null
                    .set(EMPLOYEE_CERTIFICATE.CERTIFICATE_NUMBER, certificateNumber) // may be null
                    .set(EMPLOYEE_CERTIFICATE.STATUS, status)
                    .execute();

        } catch (DataAccessException e) {
            String msg = (e.getMessage() == null) ? "" : e.getMessage().toLowerCase();

            // only treat UNIQUE/PK as "already assigned"
            if (msg.contains("unique constraint failed") || msg.contains("primary key")) {
                throw new IllegalStateException("This certificate is already assigned to the employee.", e);
            }
            if (msg.contains("foreign key constraint failed")) {
                throw new IllegalStateException("Invalid employee or certificate id (foreign key constraint failed).", e);
            }
            throw new RuntimeException("Database error occurred while assigning certificate: " + e.getMessage(), e);
        }
    }

    /**
     * Checks whether a certificate assignment exists for the given employee and certificate.
     *
     * @param employeeId    employee ID
     * @param certificateId certificate ID
     * @return true if the assignment exists
     * @throws RuntimeException if a database error occurs
     */
    public boolean existsAssignment(int employeeId, int certificateId) {
        try {
            return dsl.fetchExists(
                    dsl.selectOne()
                            .from(EMPLOYEE_CERTIFICATE)
                            .where(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID.eq(employeeId))
                            .and(EMPLOYEE_CERTIFICATE.CERTIFICATE_ID.eq(certificateId))
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while checking certificate assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Loads all certificates assigned to a specific employee.
     *
     * @param employeeId employee ID
     * @return list of assigned certificates
     * @throws RuntimeException if a database error occurs
     */
    public List<AssignedCertificate> findByEmployeeId(int employeeId) {
        try {
            return dsl.select(
                            CERTIFICATE.ID.as("certificate_id"),
                            CERTIFICATE.NAME,
                            CERTIFICATE.CATEGORY,
                            CERTIFICATE.ISSUER_TYPE,
                            EMPLOYEE_CERTIFICATE.VALID_FROM,
                            EMPLOYEE_CERTIFICATE.VALID_UNTIL,
                            EMPLOYEE_CERTIFICATE.CERTIFICATE_NUMBER,
                            EMPLOYEE_CERTIFICATE.STATUS
                    )
                    .from(EMPLOYEE_CERTIFICATE)
                    .join(CERTIFICATE).on(CERTIFICATE.ID.eq(EMPLOYEE_CERTIFICATE.CERTIFICATE_ID))
                    .where(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID.eq(employeeId))
                    .orderBy(CERTIFICATE.ID.asc())
                    .fetch(this::mapAssignedCertificate);

        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while loading employee certificates: " + e.getMessage(), e);
        }
    }

    /**
     * Convenience alias for {@link #findByEmployeeId(int)}.
     *
     * @param employeeId employee ID
     * @return list of assigned certificates
     */
    public List<AssignedCertificate> findCertificatesByEmployeeId(int employeeId) {
        return findByEmployeeId(employeeId);
    }

    /**
     * Updates an existing certificate assignment.
     *
     * @param employeeId        employee ID
     * @param certificateId     certificate ID
     * @param validFrom         validity start date
     * @param validUntil        validity end date (nullable)
     * @param certificateNumber certificate reference number (nullable)
     * @param status            assignment status
     * @throws IllegalStateException if no matching assignment exists
     * @throws RuntimeException      if a database error occurs
     */
    public void updateAssignment(int employeeId,
                                 int certificateId,
                                 String validFrom,
                                 String validUntil,
                                 String certificateNumber,
                                 String status) {
        try {
            int updated = dsl.update(EMPLOYEE_CERTIFICATE)
                    .set(EMPLOYEE_CERTIFICATE.VALID_FROM, validFrom)
                    .set(EMPLOYEE_CERTIFICATE.VALID_UNTIL, validUntil)
                    .set(EMPLOYEE_CERTIFICATE.CERTIFICATE_NUMBER, certificateNumber)
                    .set(EMPLOYEE_CERTIFICATE.STATUS, status)
                    .where(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID.eq(employeeId))
                    .and(EMPLOYEE_CERTIFICATE.CERTIFICATE_ID.eq(certificateId))
                    .execute();

            if (updated == 0) {
                throw new IllegalStateException("No assignment found for this employee and certificate.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while updating certificate assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a certificate assignment from an employee.
     *
     * @param employeeId    employee ID
     * @param certificateId certificate ID
     * @throws IllegalStateException if no matching assignment exists
     * @throws RuntimeException      if a database error occurs
     */
    public void removeAssignment(int employeeId, int certificateId) {
        try {
            int deleted = dsl.deleteFrom(EMPLOYEE_CERTIFICATE)
                    .where(EMPLOYEE_CERTIFICATE.EMPLOYEE_ID.eq(employeeId))
                    .and(EMPLOYEE_CERTIFICATE.CERTIFICATE_ID.eq(certificateId))
                    .execute();

            if (deleted == 0) {
                throw new IllegalStateException("No assignment found for this employee and certificate.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error while removing certificate assignment: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a jOOQ {@link Record} to an {@link AssignedCertificate}.
     *
     * @param r database record
     * @return mapped AssignedCertificate instance
     */
    private AssignedCertificate mapAssignedCertificate(Record r) {
        return new AssignedCertificate(
                r.get("certificate_id", Integer.class),
                r.get(CERTIFICATE.NAME),
                r.get(CERTIFICATE.CATEGORY),
                r.get(CERTIFICATE.ISSUER_TYPE),
                r.get(EMPLOYEE_CERTIFICATE.VALID_FROM),
                r.get(EMPLOYEE_CERTIFICATE.VALID_UNTIL),
                r.get(EMPLOYEE_CERTIFICATE.CERTIFICATE_NUMBER),
                r.get(EMPLOYEE_CERTIFICATE.STATUS)
        );
    }
}