package de.htwsaar.application;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.repository.EmployeeCertificateRepository;
import de.htwsaar.util.DateFormatChecker;
import de.htwsaar.domain.AssignedCertificate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CertificateService {

    private final CertificateRepository repo;
    private final EmployeeCertificateRepository employeeCertRepo; // kann null sein, wenn alter Konstruktor genutzt wird

    // Alter Konstruktor (falls ihr ihn noch irgendwo nutzt)
    public CertificateService(CertificateRepository repo) {
        this(repo, null);
    }

    // Neuer Konstruktor für Assign-Feature
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
        if (requiresRenewal) {
            if (renewalCycleMonths == null || renewalCycleMonths <= 0) {
                throw new IllegalArgumentException("Renewal cycle must be greater than 0 if renewal is required.");
            }
        } else {
            renewalCycleMonths = null; // erzwingen
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

    // Assign certificate to employee (mapped to your DB schema: valid_from / valid_until / certificate_number / status)
    public void assignCertificateToEmployee(int employeeId, int certificateId, String assignedDate) {

        if (employeeCertRepo == null) {
            throw new IllegalStateException("EmployeeCertificateRepository not configured in CertificateService.");
        }

        if (assignedDate == null || assignedDate.isBlank() || !DateFormatChecker.check(assignedDate)) {
            throw new IllegalArgumentException("Invalid assigned date. Use yyyy-mm-dd.");
        }

        Optional<Certificate> certOpt = repo.findById(certificateId);
        if (certOpt.isEmpty()) {
            throw new IllegalArgumentException("Certificate with ID " + certificateId + " not found.");
        }

        Certificate cert = certOpt.get();

        String validFrom = assignedDate;
        String validUntil = null;

        if (cert.isRequiresRenewal()) {
            Integer cycle = cert.getRenewalCycleMonths();
            if (cycle != null && cycle > 0) {
                validUntil = LocalDate.parse(assignedDate).plusMonths(cycle).toString();
            }
        }

        String certificateNumber = null; // optional später generieren
        String status = "ACTIVE";

        employeeCertRepo.assign(employeeId, certificateId, validFrom, validUntil, certificateNumber, status);
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
}