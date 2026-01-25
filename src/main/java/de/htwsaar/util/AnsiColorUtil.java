package de.htwsaar.util;

public final class AnsiColorUtil {

    private AnsiColorUtil() {}

    public static final String RESET  = "\u001B[0m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED    = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";

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