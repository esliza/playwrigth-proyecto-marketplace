package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void openCart() {
        page.click("a[href='/carrito']");
    }

    public void proceedToCheckout() {
        page.click("button:has-text(\"Proceder al pago\")");
    }

    public void increaseQuantity(int times) {
        for (int i = 0; i < times; i++) {
            try {
                page.click("button[aria-label=\"Aumentar cantidad\"]",
                        new com.microsoft.playwright.Page.ClickOptions().setTimeout(2000));
            } catch (Exception ignored) {
            }
        }
    }

    public void emptyCart() {
        page.click("button:has-text(\"Vaciar carrito\")");
    }
}
