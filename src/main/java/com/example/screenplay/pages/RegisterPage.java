package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class RegisterPage {
    private final Page page;

    public RegisterPage(Page page) {
        this.page = page;
    }

    public void fillName(String name) {
        page.fill("input[name=\"nombre\"]", name);
    }

    public void fillEmail(String email) {
        page.fill("input[name=\"correo\"]", email);
    }

    public void fillPhone(String phone) {
        page.fill("input[name=\"telefono\"]", phone);
    }

    public void fillPassword(String password) {
        page.fill("input[name=\"contrasena\"]", password);
    }

    public void fillConfirmPassword(String confirm) {
        page.fill("input[name=\"confirmarContrasena\"]", confirm);
    }

    public void submit() {
        page.click("button[type=submit]");
    }
}
