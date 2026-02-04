package de.htwsaar.domain;

/**
 * Represents a certificate that can be assigned to employees.
 * <p>
 * A certificate describes a qualification issued by an external
 * or internal authority and may optionally require periodic renewal.
 */
public class Certificate {
    private final int id;
    private final String name;
    private final String category;
    private final String issuerType;
    private final boolean requiresRenewal;
    private final Integer renewalCycleMonths; // can be NULL
    private final String description;         // can be NULL

    /**
     * Creates a new certificate instance.
     *
     * @param id                   unique identifier
     * @param name                 certificate name
     * @param category             certificate category
     * @param issuerType           issuing authority
     * @param requiresRenewal      whether renewal is required
     * @param renewalCycleMonths   renewal cycle in months (nullable)
     * @param description          optional description (nullable)
     */
    public Certificate(int id, String name, String category, String issuerType,
                       boolean requiresRenewal, Integer renewalCycleMonths, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.issuerType = issuerType;
        this.requiresRenewal = requiresRenewal;
        this.renewalCycleMonths = renewalCycleMonths;
        this.description = description;
    }

    /** @return the certificate ID */
    public int getId() { return id; }

    /** @return certificate name */
    public String getName() { return name; }

    /** @return certificate category */
    public String getCategory() { return category; }

    /** @return issuing authority */
    public String getIssuerType() { return issuerType; }

    /** @return true if renewal is required */
    public boolean isRequiresRenewal() { return requiresRenewal; }

    /** @return renewal cycle in months, or null if not applicable */
    public Integer getRenewalCycleMonths() { return renewalCycleMonths; }

    /** @return optional description */
    public String getDescription() { return description; }

    /**
     * Returns a string representation of the certificate.
     *
     * @return certificate details as string
     */
    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", issuerType='" + issuerType + '\'' +
                ", requiresRenewal=" + requiresRenewal +
                ", renewalCycleMonths=" + renewalCycleMonths +
                ", description='" + description + '\'' +
                '}';
    }
}