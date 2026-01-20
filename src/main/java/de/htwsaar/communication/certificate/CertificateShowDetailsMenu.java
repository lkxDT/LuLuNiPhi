package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class CertificateShowDetailsMenu {
    protected static void show(OutputRenderer out, InputReader in, CertificateService certService) {
        System.out.print("Certificate ID: ");
        int id;
        try {
            id = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        certService.findCertificateById(id).ifPresentOrElse(cert -> {
            System.out.println("--------------------------------------------------");
            System.out.println("Certificate Details");
            System.out.println("--------------------------------------------------");
            System.out.println("ID: " + cert.getId());
            System.out.println("Name: " + cert.getName());
            System.out.println("Category: " + cert.getCategory());
            System.out.println("Issuer: " + cert.getIssuerType());
            System.out.println("Requires renewal: " + (cert.isRequiresRenewal() ? "YES" : "NO"));
            System.out.println("Renewal cycle: " +
                    (cert.getRenewalCycleMonths() != null
                            ? cert.getRenewalCycleMonths() + " months"
                            : "-"));
            System.out.println("Description: " +
                    (cert.getDescription() != null && !cert.getDescription().isBlank()
                            ? cert.getDescription()
                            : "-"));
            System.out.println("--------------------------------------------------");
        }, () -> out.error("Certificate not found."));
    }
}
