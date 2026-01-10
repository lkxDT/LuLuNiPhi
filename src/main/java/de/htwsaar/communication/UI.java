package de.htwsaar.communication;
import java.util.Scanner;
public class UI {
    private final Scanner inputScanner;

    public UI() {
         this.inputScanner = new Scanner(System.in);
    }

    public int getInputNumber(int max) throws IllegalArgumentException {
        String input = inputScanner.next();
        int numerizedInput = Integer.parseInt(input);
        if(numerizedInput < 1 || numerizedInput > max) {
            throw new IllegalArgumentException("No valid option");
        }
        return (numerizedInput);
    }

    public void startMainMenu() {
        System.out.println("Main Menu:\n" +
                "1. Employees\n" +
                "2. Skills\n" +
                "3. Certificates\n" +
                "4. Projects"  );

        switch (this.getInputNumber(4)) {
            case 1 -> {employeeMenu();}
            case 2 -> {skillsMenu();}
            case 3 -> {certificatesMenu();}
            case 4 -> {projectsMenu();}
        }
    }

    public void employeeMenu() {
        System.out.println("Eployees Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n"  );

        switch (this.getInputNumber(3)) {
            case 1 -> {System.out.println("placeholder");}
            case 2 -> {System.out.println("placeholder");}
            case 3 -> {System.out.println("placeholder");}
        }
    }

    public void skillsMenu() {
        System.out.println("Skills Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n"  );

        switch (this.getInputNumber(3)) {
            case 1 -> {System.out.println("placeholder");}
            case 2 -> {System.out.println("placeholder");}
            case 3 -> {System.out.println("placeholder");}
        }
    }

    public void certificatesMenu() {
        System.out.println("Certifiates Menu:\n" +
                "1. show all\n" +
                "2. add\n" +
                "3. change\n"  );

        switch (this.getInputNumber(3)) {
            case 1 -> {System.out.println("placeholder");}
            case 2 -> {System.out.println("placeholder");}
            case 3 -> {System.out.println("placeholder");}
        }
    }

    public void projectsMenu() {
        System.out.println("Projects Menu:\n" +
                "1. show all\n" +
                "2. new project\n" +
                "3. change employees\n" +
                "4. change needed skills"   );

        switch (this.getInputNumber(4)) {
            case 1 -> {System.out.println("placeholder");}
            case 2 -> {System.out.println("placeholder");}
            case 3 -> {System.out.println("placeholder");}
            case 4 -> {System.out.println("placeholder");}
            case 5 -> {System.out.println("placeholder");}
        }
    }

    public void newProjectMenu() {
        System.out.println("employee selection for new Project:\n" +
                "1. manual\n" +
                "2. automatic"  );

        switch (this.getInputNumber(2)) {
            case 1 -> {System.out.println("placeholder");}
            case 2 -> {System.out.println("placeholder");}
        }
    }

}
