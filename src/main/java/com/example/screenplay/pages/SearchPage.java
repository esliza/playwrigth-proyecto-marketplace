package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class SearchPage {
    private final Page page;

    public SearchPage(Page page) {
        this.page = page;
    }

    public void openHome() {
        page.navigate("http://localhost:5173");
    }

    public void search(String query) {
        page.fill("input[aria-label=\"Buscar productos\"]", query);
    }

    public void openResultByText(String text) {
        page.click("text=" + text);
    }

    public void openResultById(String id) {
        if (id == null || id.isEmpty())
            return;
        // If it's a full URL, navigate directly
        try {
            if (id.startsWith("http")) {
                page.navigate(id);
                return;
            }
            if (id.startsWith("/")) {
                page.navigate("http://localhost:5173" + id);
                return;
            }
        } catch (Exception ignored) {
        }

        String[] selectors = new String[] {
                "a[data-id=\"" + id + "\"]",
                "a[href*=\"" + id + "\"]",
                "[data-test-result-id=\"" + id + "\"]",
                "a:has-text(\"" + id + "\")"
        };

        for (String sel : selectors) {
            try {
                page.waitForSelector(sel, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(2500));
                page.click(sel);
                return;
            } catch (Exception ignored) {
            }
        }

        // Last resort: navigate to product detail assuming standard route
        try {
            page.navigate("http://localhost:5173/productos/" + id);
        } catch (Exception ignored) {
        }
    }
}
