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

    public static String dbPath;
    public static String dbUrl;

    public static Connection openConnection(String db_Path) throws SQLException {
        dbPath = db_Path;
        dbUrl = String.format("jdbc:sqlite:%s", dbPath);
        Connection conn = DriverManager.getConnection(dbUrl);
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