package com.example.screenplay.utils;

import com.example.screenplay.abilities.UsePlaywright;

/**
 * Small helper to obtain a `UsePlaywright` ability configured from system
 * properties.
 */
public class PlaywrightManager {

    /**
     * Start Playwright using system properties (see
     * `UsePlaywright.createWithSystemProperties`).
     */
    public static UsePlaywright startChrome() {
        return UsePlaywright.createWithSystemProperties(null);
    }

    /**
     * Convenience overload to force headless mode programmatically.
     */
    public static UsePlaywright startChrome(boolean headless) {
        System.setProperty("playwright.headless", Boolean.toString(headless));
        return UsePlaywright.createWithSystemProperties(null);
    }

    public static UsePlaywright startChrome(boolean headless, String testDisplayName) {
        System.setProperty("playwright.headless", Boolean.toString(headless));
        return UsePlaywright.createWithSystemProperties(testDisplayName);
    }
}
