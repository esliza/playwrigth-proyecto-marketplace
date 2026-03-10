package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class ProductPage {
    private final Page page;

    public ProductPage(Page page) {
        this.page = page;
    }

    public void addToCart() {
        String[] selectors = new String[] {
                "button:has-text(\"Agregar al carrito\")",
                "button:has-text(\"Añadir al carrito\")",
                "button:has-text(\"Añadir\")",
                "button:has-text(\"Agregar\")",
                "button[aria-label*='Agregar']",
                "button[aria-label*='Añadir']",
                "button[type=submit]"
        };

        for (String sel : selectors) {
            try {
                page.waitForSelector(sel, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(3000));
                page.click(sel);
                return;
            } catch (Exception ignored) {
            }
        }
        // If none matched, attempt a direct click and let Playwright throw
        page.click("button:has-text(\"Agregar al carrito\")");
    }

    public void buyNow() {
        page.click("button:has-text(\"Comprar ahora\")");
    }

    public boolean hasBadgeText(String badgeText) {
        try {
            return page.isVisible("span:has-text(\"" + badgeText + "\")");
        } catch (Exception e) {
            return false;
        }
    }

    public void open(String url) {
        page.navigate(url);
    }
}
