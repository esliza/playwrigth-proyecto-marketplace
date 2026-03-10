package com.example.screenplay.tests;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class TestResultWatcher implements TestWatcher {
    private volatile boolean failed = false;

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        failed = true;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        failed = false;
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        failed = true;
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // leave as not failed
    }

    public boolean isFailed() {
        return failed;
    }
}
