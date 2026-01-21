package de.htwsaar.application;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;
import de.htwsaar.repository.EmployeeCertificateRepository;
import de.htwsaar.util.DateFormatChecker;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CertificateService {

    private final CertificateRepository repo;
    private final EmployeeCertificateRepository employeeCertRepo; // could be null if legacy constructor is used

    /**
     * legacy constructor
     * @param repo CertificateRepoisitory holding Certificates
     */
    public CertificateService(CertificateRepository repo) {
        this(repo, null);
    }

    /**
     *
     * @param repo              CertificateRepoisitory holding Certificates
     * @param employeeCertRepo  EmployeeCertificateRepository holding relations between employees and Certificates
     */
    public CertificateService(CertificateRepository repo, EmployeeCertificateRepository employeeCertRepo) {
        this.repo = repo;
        this.employeeCertRepo = employeeCertRepo;
    }

    /**
     *
     * @return List<Certificate> list of all certificates
     */
    public List<Certificate> findAllCertificates() {
        return repo.findAll();
    }

    /**
     *
     * @param id certificate id
     * @return   Optional<Certificate> Certificate
     */
    public Optional<Certificate> findCertificateById(int id) {
        return repo.findById(id);
    }

    /**
     * creates a new certificate, validates the input data and persists it in the repository
     * @param name                  name of certificate (gets trimmt)
     * @param category              category ("it", "security", ...)
     * @param issuerType            organisation or type of the issuing body
     * @param requiresRenewal       true if certificate expires
     * @param renewalCycleMonths    duration until renewal in months (must be > 0 if requiresRenewal is true)
     * @param description           additional details about certificate (optional can be null)
     * @return successfully created certificate object
     */
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

    /**
     * update the repository with the data contained in the certificate object
     * @param cert certificate to be updated must not be null should correspond to an existing record
     */
    public void updateCertificate(Certificate cert) {
        repo.update(cert);
    }

    /**
     * assigns the certificate to a specific employee and calculates the validity period
     * @param employeeId    unique id of the employee receiving the certificate
     * @param certificateId the unique id of the certificate to be assigned
     * @param assignedDate  the date the certificate was issued in "yyyy-mm-dd" formate
     */
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

        Certificate cert = certOpt.get(); // setting cert to the certificate we got from optional (must exist checked above)

        String validFrom = assignedDate;
        String validUntil = null;

        // checking if renewal is needed and setting renewal date based on local date and renewal cycle
        if (cert.isRequiresRenewal()) {
            Integer cycle = cert.getRenewalCycleMonths();
            if (cycle != null && cycle > 0) {
                validUntil = LocalDate.parse(assignedDate).plusMonths(cycle).toString();
            }
        }

        String certificateNumber = null; // will be generated later
        String status = "ACTIVE";

        employeeCertRepo.assign(employeeId, certificateId, validFrom, validUntil, certificateNumber, status);
    }

    /**
     * checks if given string is invalid (either, null or blank)
     * @param v     the string value to be validated
     * @return      {@code true} if the string is null or blank ; {@code false} otherwise.
     * @see         String#isBlank()
     */
    private boolean invalid(String v) {
        return v == null || v.isBlank();
    }
}