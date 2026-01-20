package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.*;
import de.htwsaar.domain.Certificate;
import de.htwsaar.domain.Skill;

import java.util.List;

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
        out.section("Certificates");
        System.out.println("1. Show all");
        System.out.println("2. Show details (by ID)");
        System.out.println("3. Add");
        System.out.println("4. Change");
        System.out.println("5. Return to main");

        switch (in.readMenuNumber(5)) {
            case 1 -> out.certificateTable(certService.findAllCertificates());

            case 2 -> {
                System.out.print("Enter certificate ID: ");
                int id;
                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID. Please enter a whole number.");
                    return;
                }

                certService.findCertificateById(id).ifPresentOrElse(cert -> {
                    System.out.println("--------------------------------------------------");
                    System.out.println("Certificate Details");
                    System.out.println("--------------------------------------------------");
                    System.out.println("ID: " + cert.getId());
                    System.out.println("Name: " + cert.getName());
                    System.out.println("Category: " + cert.getCategory());
                    System.out.println("Issuer Type: " + cert.getIssuerType());
                    System.out.println("Requires Renewal: " + (cert.isRequiresRenewal() ? "YES" : "NO"));
                    System.out.println("Renewal Cycle (months): " +
                            (cert.getRenewalCycleMonths() != null ? cert.getRenewalCycleMonths() : "-"));
                    System.out.println("Description: " +
                            (cert.getDescription() != null && !cert.getDescription().isBlank()
                                    ? cert.getDescription()
                                    : "-"));
                    System.out.println("--------------------------------------------------");
                }, () -> out.error("Certificate with ID " + id + " not found."));
            }

            case 3 -> {
                out.section("Add Certificate");

                System.out.print("Certificate name: ");
                String name = in.readLine();

                System.out.print("Category (e.g. Quality, Security, Regulatory): ");
                String category = in.readLine();

                System.out.print("Issuer type (e.g. ISO, IEC, Regulation, Audit): ");
                String issuerType = in.readLine();

                System.out.print("Requires renewal? (y/n): ");
                String renewalInput = in.readLine().trim().toLowerCase();
                boolean requiresRenewal = renewalInput.equals("y") || renewalInput.equals("yes");

                Integer renewalCycleMonths = null;
                if (requiresRenewal) {
                    System.out.print("Renewal cycle in months (e.g. 12, 24, 36): ");
                    String monthsStr = in.readLine().trim();
                    try {
                        renewalCycleMonths = Integer.parseInt(monthsStr);
                    } catch (NumberFormatException e) {
                        out.error("Invalid number. Certificate creation cancelled.");
                        return;
                    }
                }

                System.out.print("Description (optional, press Enter to skip): ");
                String description = in.readLine();

                try {
                    Certificate created = certService.createCertificate(
                            name, category, issuerType, requiresRenewal, renewalCycleMonths, description
                    );
                    out.info("Certificate successfully created.");
                    System.out.println(created);
                } catch (RuntimeException e) {
                    out.error("Certificate creation failed: " + e.getMessage());
                }
            }

            case 4 -> {
                System.out.print("Enter certificate ID to change: ");
                int id;

                try {
                    id = Integer.parseInt(in.readLine().trim());
                } catch (NumberFormatException e) {
                    out.error("Invalid ID.");
                    return;
                }

                certService.findCertificateById(id).ifPresentOrElse(cert -> {

                    out.info("Press Enter to keep current value.");

                    System.out.print("Name [" + cert.getName() + "]: ");
                    String name = in.readLine();
                    if (!name.isBlank()) cert = new Certificate(
                            cert.getId(),
                            name,
                            cert.getCategory(),
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            cert.getDescription()
                    );

                    System.out.print("Category [" + cert.getCategory() + "]: ");
                    String category = in.readLine();
                    if (!category.isBlank()) cert = new Certificate(
                            cert.getId(),
                            cert.getName(),
                            category,
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            cert.getDescription()
                    );

                    System.out.print("Description [" +
                            (cert.getDescription() != null ? cert.getDescription() : "-") + "]: ");
                    String desc = in.readLine();
                    if (!desc.isBlank()) cert = new Certificate(
                            cert.getId(),
                            cert.getName(),
                            cert.getCategory(),
                            cert.getIssuerType(),
                            cert.isRequiresRenewal(),
                            cert.getRenewalCycleMonths(),
                            desc
                    );

                    try {
                        certService.updateCertificate(cert);
                        out.info("Certificate updated successfully.");
                    } catch (RuntimeException e) {
                        out.error("Certificate update failed: " + e.getMessage());
                    }

                }, () -> out.error("Certificate not found."));
            }

            //case 5 -> shouldExit = true;
        }

        //certificatesMenu();
    }

}
