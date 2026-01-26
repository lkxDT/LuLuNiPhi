package de.htwsaar.util;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.Statement;

public final class JooqContext {

    private JooqContext() {}

    public static DSLContext using(Connection conn) {
        enableForeignKeys(conn);
        return DSL.using(conn, SQLDialect.SQLITE);
    }

    private static void enableForeignKeys(Connection conn) {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (Exception ignored) {
            // optional: log if you want
        }
    }
}