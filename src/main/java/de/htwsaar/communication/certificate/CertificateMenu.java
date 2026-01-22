package de.htwsaar.communication.certificate;


import de.htwsaar.application.CertificateService;
import de.htwsaar.communication.InputReader;
import de.htwsaar.communication.OutputRenderer;
import de.htwsaar.application.EmployeeService;

public class CertificateMenu {

    private final CertificateService certService;
    private final InputReader in;
    private final OutputRenderer out;
    private final EmployeeService empService;

    public CertificateMenu(
            EmployeeService empService,
            CertificateService certService,
            InputReader in,
            OutputRenderer out
    ) {
        this.empService = empService;
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
            System.out.println("5. Assign certificate to employee");
            System.out.println("6. Return");

            switch (in.readMenuNumber(6)) {
                case 1 -> out.certificateTable(certService.findAllCertificates());
                case 2 -> CertificateShowDetailsMenu.show(out, in, certService);
                case 3 -> CertificateAddMenu.show(out, in, certService);
                case 4 -> CertificateChangeMenu.show(out, in, certService);
                case 5 -> CertificateAssignMenu.show(out, in, empService, certService);
                case 6 -> back = true;
            }
        }
    }
}