package de.htwsaar.application;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;

import java.util.List;

public class CertificateService {

    private final CertificateRepository repo;

    public CertificateService(CertificateRepository repo) {
        this.repo = repo;
    }

    public List<Certificate> findAllCertificates() {
        return repo.findAll();
    }
}