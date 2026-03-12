package com.example.screenplay.pages;

import com.example.screenplay.config.Config;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;

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
        page.fill("input[name=\"correo\"]", email);
    }

    public void fillPassword(String password) {
        page.fill("input[name=\"contrasena\"]", password);
    }

    public void submit() {
        page.click("button[type=submit]");
    }
}
