package de.htwsaar.communication.certificate;

import java.util.List;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.domain.Certificate;

public class CertificateMenu {

    private final CertificateService certService;
    private final InputReader in;
    private final OutputRenderer out;

    public CertificateMenu(
            CertificateService certService,
            InputReader in,
            OutputRenderer out
    ) {
        this.certService = certService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Certificates");
            System.out.println("1. Show all");
            System.out.println("2. Show details");
            System.out.println("3. Add");
            System.out.println("4. Change");
            System.out.println("5. Return");

            switch (in.readMenuNumber(5)) {
                case 1 -> out.certificateTable(certService.findAllCertificates());
                case 2 -> CertificateShowDetailsMenu.show(out, in, certService);
                case 3 -> CertificateAddMenu.show(out, in, certService);
                case 4 -> CertificateChangeMenu.show(out, in, certService);
                case 5 -> back = true;
            }
        }
    }

    private void changeCertificate() {
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
