package de.htwsaar.domain;

/**
 * Represents a certificate that is assigned to an employee.
 * <p>
 * This class is a read-only data transfer object containing
 * certificate metadata together with assignment-specific
 * information such as validity dates, certificate number
 * and current status.
 */
public class AssignedCertificate {

    private final int certificateId;
    private final String name;
    private final String category;
    private final String issuerType;

    private final String validFrom;
    private final String validUntil;
    private final String certificateNumber;
    private final String status;

    /**
     * Creates a new assigned certificate instance.
     *
     * @param certificateId     unique identifier of the certificate
     * @param name              name of the certificate
     * @param category          category of the certificate
     * @param issuerType        issuing organization or authority
     * @param validFrom         start date of validity
     * @param validUntil        end date of validity (may be null)
     * @param certificateNumber certificate reference number (may be null)
     * @param status            current status of the certificate
     */
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

    /**
     * @return the certificate ID
     */
    public int getCertificateId() { return certificateId; }

    /**
     * @return the certificate name
     */
    public String getName() { return name; }

    /**
     * @return the certificate category
     */
    public String getCategory() { return category; }

    /**
     * @return the issuer type
     */
    public String getIssuerType() { return issuerType; }

    /**
     * @return the validity start date
     */
    public String getValidFrom() { return validFrom; }

    /**
     * @return the validity end date or null if unlimited
     */
    public String getValidUntil() { return validUntil; }

    /**
     * @return the certificate number or null if not available
     */
    public String getCertificateNumber() { return certificateNumber; }

    /**
     * @return the current certificate status
     */
    public String getStatus() { return status; }
}