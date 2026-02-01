package de.htwsaar.util;

import java.io.File;
import java.io.IOException;

public final class DBFileHandler {
    private DBFileHandler(String path) {}

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
