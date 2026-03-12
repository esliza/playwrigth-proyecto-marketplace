package com.example.screenplay.pages;

import com.example.screenplay.config.Config;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private static final String[] SELECTORS_EMAIL = new String[] {
            "input[name=\"correo\"]",
            "input[name=\"email\"]",
            "input[type=\"email\"]",
            "input[placeholder*='Correo']",
            "input[placeholder*='Email']",
            "#email",
    };
    private static final String[] SELECTORS_PASSWORD = new String[] {
            "input[name=\"contrasena\"]",
            "input[name=\"password\"]",
            "input[type=\"password\"]",
            "input[placeholder*='Contraseña']",
            "#password",
    };
    private static final String[] SELECTORS_SUBMIT = new String[] {
            "button[type=submit]",
            "button:has-text('Ingresar')",
            "button:has-text('Iniciar sesión')",
            "button:has-text('Entrar')",
    };

    public LoginPage(Page page) {
        this.page = page;
    }

    public String url() {
        return Config.baseUrl() + "/login";
    }

    public void open() {
        try {
            page.navigate(url(), new com.microsoft.playwright.Page.NavigateOptions().setTimeout(60000));
        } catch (Exception e) {
            try {
                Thread.sleep(2000);
                page.navigate(url(), new com.microsoft.playwright.Page.NavigateOptions().setTimeout(60000));
            } catch (Exception ex) {
                throw new RuntimeException("Failed to navigate to login page: " + url(), ex);
            }
        }
    }

    public void fillEmail(String email) {
        for (String sel : SELECTORS_EMAIL) {
            try {
                com.microsoft.playwright.Locator locator = page.locator(sel);
                if (locator.count() > 0) {
                    locator.first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(10000));
                    locator.first().fill(email);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        // fallback
        page.fill(SELECTORS_EMAIL[0], email);
    }

    public void fillPassword(String password) {
        for (String sel : SELECTORS_PASSWORD) {
            try {
                com.microsoft.playwright.Locator locator = page.locator(sel);
                if (locator.count() > 0) {
                    locator.first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(10000));
                    locator.first().fill(password);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        page.fill(SELECTORS_PASSWORD[0], password);
    }

    public void submit() {
        for (String sel : SELECTORS_SUBMIT) {
            try {
                com.microsoft.playwright.Locator locator = page.locator(sel);
                if (locator.count() > 0) {
                    locator.first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(5000));
                    locator.first().click(new com.microsoft.playwright.Locator.ClickOptions().setTimeout(10000));
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        page.click(SELECTORS_SUBMIT[0]);
    }
}
