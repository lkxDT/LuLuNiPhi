package de.htwsaar.domain;

public class Certificate {
    private final int id;
    private final String name;
    private final String category;
    private final String issuerType;
    private final boolean requiresRenewal;
    private final Integer renewalCycleMonths; // kann NULL sein
    private final String description;         // kann NULL sein

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

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getIssuerType() { return issuerType; }
    public boolean isRequiresRenewal() { return requiresRenewal; }
    public Integer getRenewalCycleMonths() { return renewalCycleMonths; }
    public String getDescription() { return description; }

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