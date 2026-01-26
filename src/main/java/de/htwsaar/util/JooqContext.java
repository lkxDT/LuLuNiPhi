package de.htwsaar.util;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class JooqContext {

    private JooqContext() {}

    public static final String DB_PATH = "data/employees.db";
    public static final String DB_URL  = "jdbc:sqlite:" + DB_PATH;

    public static Connection openConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        enableForeignKeys(conn);
        return conn;
    }

    public static DSLContext using(Connection conn) {
        return DSL.using(conn, SQLDialect.SQLITE);
    }

    private static void enableForeignKeys(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enable foreign keys", e);
        }
    }
}