package de.htwsaar;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

public class MuteConsoleExtension implements BeforeEachCallback, AfterEachCallback {
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Override
    public void beforeEach(ExtensionContext context) {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}