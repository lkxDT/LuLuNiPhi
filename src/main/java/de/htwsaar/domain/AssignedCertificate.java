package de.htwsaar.domain;

public class AssignedCertificate {

    private final int certificateId;
    private final String name;
    private final String category;
    private final String issuerType;

    private final String validFrom;
    private final String validUntil;
    private final String certificateNumber;
    private final String status;

    public AssignedCertificate(int certificateId,
                               String name,
                               String category,
                               String issuerType,
                               String validFrom,
                               String validUntil,
                               String certificateNumber,
                               String status) {
        this.certificateId = certificateId;
        this.name = name;
        this.category = category;
        this.issuerType = issuerType;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.certificateNumber = certificateNumber;
        this.status = status;
    }

    public int getCertificateId() { return certificateId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getIssuerType() { return issuerType; }
    public String getValidFrom() { return validFrom; }
    public String getValidUntil() { return validUntil; }
    public String getCertificateNumber() { return certificateNumber; }
    public String getStatus() { return status; }
}