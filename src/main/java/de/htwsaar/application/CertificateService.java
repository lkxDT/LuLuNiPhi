package de.htwsaar.application;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.repository.EmployeeCertificateRepository;
import de.htwsaar.util.DateFormatChecker;
import de.htwsaar.domain.AssignedCertificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class CertificateService {

    private final CertificateRepository repo;
    private final EmployeeCertificateRepository employeeCertRepo; // could be null if legacy constructor is used

    public CertificateService(CertificateRepository repo) {
        this(repo, null);
    }

    public CertificateService(CertificateRepository repo, EmployeeCertificateRepository employeeCertRepo) {
        this.repo = repo;
        this.employeeCertRepo = employeeCertRepo;
    }

    public List<Certificate> findAllCertificates() {
        return repo.findAll();
    }

    public Optional<Certificate> findCertificateById(int id) {
        return repo.findById(id);
    }

    public Certificate createCertificate(String name,
                                         String category,
                                         String issuerType,
                                         boolean requiresRenewal,
                                         Integer renewalCycleMonths,
                                         String description) {


        if (invalid(name) || invalid(category) || invalid(issuerType)) {
            throw new IllegalArgumentException("Name, category and issuer type must be filled out.");
        }

        if (repo.existsByNameAndIssuer(name, issuerType)) {
            throw new IllegalArgumentException(
                    "Certificate' " + name + "' from issuer'" + issuerType + "' already exists."
            );
        }
        // when requiresRenewal is true, check if renewalCycle is set
        if (requiresRenewal) {
            if (renewalCycleMonths == null || renewalCycleMonths <= 0) {
                throw new IllegalArgumentException("Renewal cycle must be greater than 0 if renewal is required.");
            }
        } else {
            renewalCycleMonths = null; // when requireRenewal is false, force null
        }

        return repo.save(
                name.trim(),
                category.trim(),
                issuerType.trim(),
                requiresRenewal,
                renewalCycleMonths,
                (description == null ? null : description.trim())
        );
    }

    public void updateCertificate(Certificate cert) {
        repo.update(cert);
    }


    public void assignCertificateToEmployee(int employeeId, int certificateId, String assignedDate) {

        if (employeeCertRepo == null) {
            throw new IllegalStateException("EmployeeCertificateRepository not configured in CertificateService.");
        }

        if (assignedDate == null || assignedDate.isBlank() || !DateFormatChecker.check(assignedDate)) {
            throw new IllegalArgumentException("Invalid assigned date. Use yyyy-mm-dd.");
        }

        repo.ctx().transaction(cfg -> {
            DSLContext tx = DSL.using(cfg);

            var certRepoTx = new CertificateRepository(tx);
            var empCertRepoTx = new EmployeeCertificateRepository(tx);

            Certificate cert = certRepoTx.findById(certificateId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Certificate with ID " + certificateId + " not found."
                    ));

            String validFrom = assignedDate;
            String validUntil = null;

            if (cert.isRequiresRenewal()) {
                Integer cycle = cert.getRenewalCycleMonths();
                if (cycle != null && cycle > 0) {
                    validUntil = LocalDate.parse(assignedDate).plusMonths(cycle).toString();
                }
            }

            String certificateNumber = null;
            String status = "ACTIVE";

            empCertRepoTx.assign(employeeId, certificateId, validFrom, validUntil, certificateNumber, status);
        });
    }

    private boolean invalid(String v) {
        return v == null || v.isBlank();
    }

    public List<AssignedCertificate> findCertificatesOfEmployee(int employeeId) {
        if (employeeCertRepo == null) {
            throw new IllegalStateException("EmployeeCertificateRepository not configured.");
        }
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        return employeeCertRepo.findByEmployeeId(employeeId);
    }
    public void deleteCertificateById(int certId) {
        if (certId <= 0) throw new IllegalArgumentException("Invalid certificate ID.");

        boolean deleted = repo.deleteById(certId);
        if (!deleted) {
            throw new IllegalArgumentException("Certificate with ID " + certId + " not found.");
        }
    }
    public void updateCertificateAssignment(int employeeId,
                                            int certificateId,
                                            String assignedDate,
                                            String certificateNumber,
                                            String status) {

        if (employeeCertRepo == null) {
            throw new IllegalStateException("EmployeeCertificateRepository not configured.");
        }

        if (assignedDate == null || assignedDate.isBlank() || !DateFormatChecker.check(assignedDate)) {
            throw new IllegalArgumentException("Invalid assigned date. Use yyyy-mm-dd.");
        }

        repo.ctx().transaction(cfg -> {
            DSLContext tx = DSL.using(cfg);

            var certRepoTx = new CertificateRepository(tx);
            var empCertRepoTx = new EmployeeCertificateRepository(tx);

            if (!empCertRepoTx.existsAssignment(employeeId, certificateId)) {
                throw new IllegalStateException("This certificate is not assigned to the employee.");
            }

            Certificate cert = certRepoTx.findById(certificateId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Certificate with ID " + certificateId + " not found."
                    ));

            String validFrom = assignedDate;
            String validUntil = null;

            if (cert.isRequiresRenewal()) {
                Integer cycle = cert.getRenewalCycleMonths();
                if (cycle != null && cycle > 0) {
                    validUntil = LocalDate.parse(assignedDate).plusMonths(cycle).toString();
                }
            }

            String normalizedStatus = (status == null || status.isBlank()) ? "ACTIVE" : status.trim().toUpperCase();
            String certNo = (certificateNumber == null || certificateNumber.isBlank()) ? null : certificateNumber.trim();

            empCertRepoTx.updateAssignment(employeeId, certificateId, validFrom, validUntil, certNo, normalizedStatus);
        });
    }

    public void removeCertificateFromEmployee(int employeeId, int certificateId) {
        if (employeeCertRepo == null) {
            throw new IllegalStateException("EmployeeCertificateRepository not configured.");
        }

        repo.ctx().transaction(cfg -> {
            DSLContext tx = DSL.using(cfg);
            var empCertRepoTx = new EmployeeCertificateRepository(tx);

            if (!empCertRepoTx.existsAssignment(employeeId, certificateId)) {
                throw new IllegalStateException("This certificate is not assigned to the employee.");
            }
            empCertRepoTx.removeAssignment(employeeId, certificateId);
        });
    }
}