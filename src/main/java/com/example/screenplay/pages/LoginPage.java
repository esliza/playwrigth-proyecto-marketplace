package com.example.screenplay.pages;

import com.example.screenplay.config.Config;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private static final String SELECTOR_EMAIL = "input[name=\"correo\"]";
    private static final String SELECTOR_PASSWORD = "input[name=\"contrasena\"]";
    private static final String SELECTOR_SUBMIT = "button[type=submit]";

    public LoginPage(Page page) {
        this.page = page;
    }

    public String url() {
        return Config.baseUrl() + "/login";
    }

    public void open() {
        page.navigate(url());
    }

    public void fillEmail(String email) {
        page.fill(SELECTOR_EMAIL, email);
    }

    public void fillPassword(String password) {
        page.fill(SELECTOR_PASSWORD, password);
    }

    public void submit() {
        page.click(SELECTOR_SUBMIT);
    }
}
