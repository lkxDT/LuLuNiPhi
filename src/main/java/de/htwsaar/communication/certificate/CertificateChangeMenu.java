package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;

/**
 * Menu for updating an existing certificate.
 */
public class CertificateChangeMenu {
    /**
     * Displays the menu to modify certificate details.
     * Pressing Enter keeps the current value for each field.
     *
     * @param out         OutputRenderer for console output
     * @param in          InputReader for user input
     * @param certService Service for certificate operations
     */
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

            System.out.println("Press Enter to keep current value.");

            System.out.print("Name [" + cert.getName() + "]: ");
            String name = in.readLine();
            if (!name.isBlank()) cert = new Certificate(
                    cert.getId(), name, cert.getCategory(),
                    cert.getIssuerType(), cert.isRequiresRenewal(),
                    cert.getRenewalCycleMonths(), cert.getDescription()
            );

            System.out.print("Category [" + cert.getCategory() + "]: ");
            String category = in.readLine();
            if (!category.isBlank()) cert = new Certificate(
                    cert.getId(), cert.getName(), category,
                    cert.getIssuerType(), cert.isRequiresRenewal(),
                    cert.getRenewalCycleMonths(), cert.getDescription()
            );

            System.out.print("Description [" +
                    (cert.getDescription() != null ? cert.getDescription() : "-") + "]: ");
            String desc = in.readLine();
            if (!desc.isBlank()) cert = new Certificate(
                    cert.getId(), cert.getName(), cert.getCategory(),
                    cert.getIssuerType(), cert.isRequiresRenewal(),
                    cert.getRenewalCycleMonths(), desc
            );

            try {
                certService.updateCertificate(cert);
                out.info("Certificate updated.");
            } catch (RuntimeException e) {
                out.error(e.getMessage());
            }

        }, () -> out.error("Certificate not found."));
    }
}
