package de.htwsaar.communication;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;
    private final OutputRenderer err;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
        this.err = new OutputRenderer();
    }

    public int readMenuNumber(int max) {
        while (true) {
            if (!scanner.hasNext()) {
                return -1;
            }

            String input = scanner.nextLine();

            if (input.contains(".") || input.contains(",")) {
                err.error("Please do not use decimals or commas.");
                continue;
            }

            try {
                int numerizedInput = Integer.parseInt(input);

                if (numerizedInput < 1 || numerizedInput > max) {
                    err.error("Please enter a number between 1 and " + max + ".");
                    continue;
                }

                return numerizedInput;

            } catch (NumberFormatException e) {
                err.error("Invalid input. Please enter a whole number.");
            }
        }
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public int readIntOrFail(String errorMsg) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMsg);
        }
    }
}