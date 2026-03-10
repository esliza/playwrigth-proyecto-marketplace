package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;
    public final String url = "http://localhost:5173/";

    public HomePage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(url);
    }

    public void clickRegister() {
        page.click("a[href=\"/register\"]");
    }

    public void clickLogin() {
        page.click("a[href=\"/login\"]");
    }
}
