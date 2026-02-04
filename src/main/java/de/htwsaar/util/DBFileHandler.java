package de.htwsaar.util;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for handling database file creation.
 * <p>
 * Ensures that the data directory exists and initializes
 * the SQLite database file if it is not already present.
 */
public final class DBFileHandler {

    /**
     * Private constructor to prevent instantiation.
     */
    private DBFileHandler(String path) {}

    /**
     * Creates the database directory and file if they do not exist.
     *
     * @param path path to the database file
     * @throws RuntimeException if the directory or file cannot be created
     */
    public static void createDbFile(String path){
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            boolean dirCreated = dataDir.mkdirs();
            if (!dirCreated) {
                throw new RuntimeException("Failed to create data directory.");
            }
        }

        // Initialize the database file if it doesn't exist
        File dbFile = new File(path);
        if (!dbFile.exists()) {
            try {
                boolean fileCreated = dbFile.createNewFile();
                if (!fileCreated) {
                    throw new RuntimeException("Failed to create database file.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Error creating database file: " + e.getMessage(), e);
            }
        }
    }
}
