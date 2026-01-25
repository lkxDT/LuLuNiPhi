package de.htwsaar.communication.certificate;

import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;

public class CertificateMenu {

    private final CertificateService certService;
    private final InputReader in;
    private final OutputRenderer out;

    public CertificateMenu(CertificateService certService, InputReader in, OutputRenderer out) {
        this.certService = certService;
        this.in = in;
        this.out = out;
    }

    public void show() {
        boolean back = false;

        while (!back) {
            out.section("Certificates");
            System.out.println("1. Show All");
            System.out.println("2. Show Details");
            System.out.println("3. Add Certificate");
            System.out.println("4. Change Certificate");
            System.out.println("5. Delete Certificate");
            System.out.println("6. Return");

            switch (in.readMenuNumber(6)) {
                case 1 -> out.certificateTable(certService.findAllCertificates());
                case 2 -> CertificateShowDetailsMenu.show(out, in, certService);
                case 3 -> CertificateAddMenu.show(out, in, certService);
                case 4 -> CertificateChangeMenu.show(out, in, certService);
                case 5 -> CertificateDeleteMenu.show(out, in, certService);
                case 6 -> back = true;
            }
        }
    }
}