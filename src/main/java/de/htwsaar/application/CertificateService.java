package de.htwsaar.application;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;

import java.util.List;
import java.util.Optional;

public class CertificateService {

    private final CertificateRepository repo;

    public CertificateService(CertificateRepository repo) {
        this.repo = repo;
    }

    public List<Certificate> findAllCertificates() {
        return repo.findAll();
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
    public Optional<Certificate> findCertificateById(int id) {
        return repo.findById(id);
    }

    private boolean invalid(String v) {
        return v == null || v.isBlank();
    }

}