package com.example.screenplay.config;

public final class Config {
    private Config() {
    }

    /**
     * Returns the base URL for the AUT.
     * Priority: System property 'baseUrl' > env 'PLAYWRIGHT_BASE_URL' > default
     * http://localhost:5173
     */
    public static String baseUrl() {
        String fromProp = System.getProperty("baseUrl");
        if (fromProp != null && !fromProp.isEmpty())
            return fromProp;
        String fromEnv = System.getenv("PLAYWRIGHT_BASE_URL");
        if (fromEnv != null && !fromEnv.isEmpty())
            return fromEnv;
        return "https://condominio-marketplace.netlify.app";
    }
}
