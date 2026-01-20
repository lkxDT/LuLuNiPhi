package de.htwsaar.communication;

import java.util.Scanner;

public class InputReader {

    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readMenuNumber(int max) {
        while (true) {
            String input = scanner.nextLine();

            if (input.contains(".") || input.contains(",")) {
                System.out.println("[ERROR] Please enter a whole number.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value < 1 || value > max) {
                    System.out.println("[ERROR] Enter a number between 1 and " + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid input.");
            }
        }
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public int readInt(String errorMsg) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
