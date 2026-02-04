package de.htwsaar.util;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for managing jOOQ database contexts and connections.
 * <p>
 * Provides helper methods to open SQLite connections,
 * enable foreign key constraints and create jOOQ {@link DSLContext} instances.
 */
public final class JooqContext {

    /**
     * Private constructor to prevent instantiation.
     */
    private JooqContext() {}

    /** Current database file path */
    public static String dbPath;

    /** JDBC connection URL */
    public static String dbUrl;

    /**
     * Opens a SQLite database connection and enables foreign key constraints.
     *
     * @param db_Path path to the database file
     * @return opened JDBC connection
     * @throws SQLException if the connection cannot be created
     */
    public static Connection openConnection(String db_Path) throws SQLException {
        dbPath = db_Path;
        dbUrl = String.format("jdbc:sqlite:%s", dbPath);
        Connection conn = DriverManager.getConnection(dbUrl);
        enableForeignKeys(conn);
        return conn;
    }

    /**
     * Creates a jOOQ {@link DSLContext} for the given connection.
     *
     * @param conn database connection
     * @return DSLContext configured for SQLite
     */
    public static DSLContext using(Connection conn) {
        return DSL.using(conn, SQLDialect.SQLITE);
    }

    /**
     * Enables SQLite foreign key constraint enforcement.
     *
     * @param conn active database connection
     * @throws RuntimeException if foreign keys cannot be enabled
     */
    private static void enableForeignKeys(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable foreign keys", e);
        }
    }
}