package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;

import java.util.List;

/**
 * Menu for adding a new certificate.
 */
public class CertificateAddMenu {
    /**
     * Displays the menu to create a new certificate.
     *
     * @param out         OutputRenderer for console output
     * @param in          InputReader for user input
     * @param certService Service for certificate operations
     */
    protected static void show(OutputRenderer out, InputReader in, CertificateService certService) {
        out.section("Add Certificate");

        System.out.print("Name: ");
        String name = in.readLine();

        System.out.print("Category: ");
        String category = in.readLine();

        System.out.print("Issuer type: ");
        String issuer = in.readLine();

        System.out.print("Requires renewal? (y/n): ");
        boolean renewal = in.readLine().trim().equalsIgnoreCase("y");

        Integer cycle = null;
        if (renewal) {
            System.out.print("Renewal cycle (months): ");
            try {
                cycle = in.readInt("Invalid number.");
            } catch (IllegalArgumentException e) {
                out.error(e.getMessage());
                return;
            }
        }

        System.out.print("Description (optional): ");
        String desc = in.readLine();

        try {
            Certificate created = certService.createCertificate(
                    name, category, issuer, renewal, cycle, desc
            );
            out.info("Certificate created.");
            out.certificateTable(List.of(created));
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}
