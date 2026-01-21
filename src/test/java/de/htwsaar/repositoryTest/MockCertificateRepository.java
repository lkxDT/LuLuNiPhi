package de.htwsaar.repositoryTest;

import de.htwsaar.domain.Certificate;
import de.htwsaar.repository.CertificateRepository;
import java.util.*;

public class MockCertificateRepository extends CertificateRepository {
    private final List<Certificate> certs = new ArrayList<>();

    public MockCertificateRepository() { super(null); }

    @Override
    public List<Certificate> findAll() { return new ArrayList<>(certs); }

    @Override
    public Optional<Certificate> findById(int id) {
        return certs.stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public Certificate save(String name, String cat, String iss, boolean req, Integer cycle, String desc) {
        Certificate c = new Certificate(certs.size() + 1, name, cat, iss, req, cycle, desc);
        certs.add(c);
        return c;
    }

    @Override
    public boolean existsByNameAndIssuer(String name, String issuer) {
        if (name == null || issuer == null) return false;


        return certs.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name.trim()));
    }
}