package de.htwsaar.communication;

import java.util.Scanner;
import de.htwsaar.communication.OutputRenderer;

/**
 * Utility class for reading and validating user input from the console.
 * <p>
 * Wraps a {@link Scanner} and provides helper methods for
 * menu selection and typed input with basic validation
 * and user-friendly error messages.
 */
public class InputReader {

    private final Scanner scanner;
    private final OutputRenderer out;

    /**
     * Creates a new InputReader using the given scanner.
     *
     * @param scanner Scanner used to read console input
     */
    public InputReader(Scanner scanner) {
        this.scanner = scanner;
        this.out = new OutputRenderer();
    }

    /**
     * Reads a menu selection from the user.
     * <p>
     * Ensures the input is a whole number within the given range.
     * Keeps prompting until valid input is provided.
     *
     * @param max maximum allowed menu number
     * @return selected menu number between 1 and max (inclusive)
     */
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

    /**
     * Reads a full line of text from the console.
     *
     * @return user input line
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Reads an integer value from the console.
     *
     * @param errorMsg error message used if input is not a valid integer
     * @return parsed integer value
     * @throws IllegalArgumentException if input cannot be parsed
     */
    public int readInt(String errorMsg) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
