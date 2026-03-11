package com.example.screenplay.pages;

import com.example.screenplay.config.Config;
import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(Config.baseUrl() + "/");
    }

    public void clickRegister() {
        page.click("a[href=\"/register\"]");
    }

    public void clickLogin() {
        page.click("a[href=\"/login\"]");
    }
}
