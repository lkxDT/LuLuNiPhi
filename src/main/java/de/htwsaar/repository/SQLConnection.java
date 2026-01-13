package de.htwsaar.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private SQLConnection() {}

    /**
     * Static helper class for handling the connection
     * @return Connection to SQL Database
     * @throws SQLException for debug
     */
    public static Connection open() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:src/main/data/employees.db");
    }
}
