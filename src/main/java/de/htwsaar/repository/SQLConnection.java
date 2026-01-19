package de.htwsaar.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLConnection {

    public static final String DB_PATH = "data/employees.db";
    public static final String DB_URL  = "jdbc:sqlite:" + DB_PATH;

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}