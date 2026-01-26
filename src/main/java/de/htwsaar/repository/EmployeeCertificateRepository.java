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

public class EmployeeCertificateRepository {

    private final DSLContext dsl;

    public EmployeeCertificateRepository(Connection conn) {
        this.dsl = using(conn);
    }

    // ✅ NEU
    public EmployeeCertificateRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // ✅ NEU
    public DSLContext ctx() {
        return dsl;
    }


    // --- ASSIGN (API: wie vorher) ---
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

    // --- EXISTS ---
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

    // --- FIND BY EMPLOYEE ---
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

    public List<AssignedCertificate> findCertificatesByEmployeeId(int employeeId) {
        return findByEmployeeId(employeeId);
    }

    // --- UPDATE ---
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

    // --- REMOVE ---
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