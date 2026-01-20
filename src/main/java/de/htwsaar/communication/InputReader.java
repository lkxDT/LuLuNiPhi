package de.htwsaar.communication;

import java.util.Scanner;
import de.htwsaar.communication.OutputRenderer;

public class InputReader {

    private final Scanner scanner;
    private final OutputRenderer out;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
        this.out = new OutputRenderer();
    }

    public int readMenuNumber(int max) {
        while (true) {
            String input = scanner.nextLine();

            if (input.contains(".") || input.contains(",")) {
                out.error("Please enter a whole number.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value < 1 || value > max) {
                    out.error("Enter a number between 1 and " + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                out.error("Invalid input.");
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
