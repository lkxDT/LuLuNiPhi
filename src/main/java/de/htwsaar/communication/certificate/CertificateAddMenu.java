package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;

import java.util.List;

public class CertificateAddMenu {
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
