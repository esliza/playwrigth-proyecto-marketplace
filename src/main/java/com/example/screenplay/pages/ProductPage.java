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

        // Wait for page to stabilize (dynamic content may load late)
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                    new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(10000));
        } catch (Exception ignored) {
        }

        for (String sel : selectors) {
            try {
                com.microsoft.playwright.Locator locator = page.locator(sel);
                if (locator.count() > 0) {
                    // wait for visible and enabled
                    locator.first().waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(10000));
                    locator.first().click(new com.microsoft.playwright.Locator.ClickOptions().setTimeout(10000));
                    return;
                }
            } catch (Exception ignored) {
            }
        }

        // Last-resort JS fallback: find any button with text like 'agregar' or 'añadir'
        // and click
        try {
            String script = "() => { const buttons = Array.from(document.querySelectorAll('button')); for (const b of buttons) { const t = (b.innerText || b.textContent || '').toLowerCase(); if (t.includes('agregar') || t.includes('añadir') || t.includes('añadir al carrito') || t.includes('agregar al carrito')) { b.click(); return true; } } return false; }";
            Object res = page.evaluate(script);
            if (res instanceof Boolean && (Boolean) res)
                return;
        } catch (Exception ignored) {
        }

        // If none matched, attempt a direct click and let Playwright throw for
        // visibility into cause
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
