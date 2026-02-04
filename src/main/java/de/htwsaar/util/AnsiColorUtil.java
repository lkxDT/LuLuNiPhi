package de.htwsaar.util;

/**
 * Utility class for ANSI color formatting in console output.
 * <p>
 * Provides color constants and helper methods to apply
 * colored formatting to specific text values such as
 * employee status indicators.
 */
public final class AnsiColorUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private AnsiColorUtil() {}

    public static final String RESET  = "\u001B[0m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED    = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";

    /**
     * Applies color formatting to an employee status string.
     * <p>
     * Known statuses are mapped to specific colors and padded
     * to a fixed width for aligned console output.
     *
     * @param status status text (e.g. ACTIVE, INACTIVE, ON_LEAVE)
     * @param width  output width for padding
     * @return formatted and colored status string
     */
    public static String colorizeStatus(String status, int width) {
        if (status == null) status = "";
        String s = status.trim().toUpperCase();

        String color = switch (s) {
            case "ACTIVE"   -> GREEN;
            case "INACTIVE" -> YELLOW;
            case "ON_LEAVE" -> RED;
            default         -> "";
        };

        int pad = Math.max(0, width - s.length());
        String spaces = " ".repeat(pad);

        if (color.isEmpty()) {
            return s + spaces;
        }
        return color + s + spaces + RESET;
    }
}