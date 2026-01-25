package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class CertificateDeleteMenu {
    private CertificateDeleteMenu() {}

    public static void show(OutputRenderer out, InputReader in, CertificateService certService) {
        out.section("Delete Certificate");

        System.out.print("Certificate ID: ");
        int id;
        try {
            id = in.readInt("Invalid ID.");
        } catch (IllegalArgumentException e) {
            out.error(e.getMessage());
            return;
        }

        var certOpt = certService.findCertificateById(id);
        if (certOpt.isEmpty()) {
            out.error("Certificate with ID " + id + " not found.");
            return;
        }

        out.certificateTable(java.util.List.of(certOpt.get()));

        System.out.print("Are you sure you want to delete this certificate? (y/N): ");
        String confirm = in.readLine();
        if (!confirm.equalsIgnoreCase("y")) {
            out.info("Delete canceled.");
            return;
        }

        try {
            certService.deleteCertificateById(id);
            out.info("Certificate deleted.");
        } catch (RuntimeException e) {
            out.error(e.getMessage());
        }
    }
}