package com.example.screenplay.questions;

import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Question;
import com.microsoft.playwright.Page;
import com.example.screenplay.pages.ProductPage;

public class ProductHasBadge implements Question<Boolean> {
    private final String badgeText;
    private final int timeoutMs;

    public ProductHasBadge(String badgeText) {
        this(badgeText, 5000);
    }

    public ProductHasBadge(String badgeText, int timeoutMs) {
        this.badgeText = badgeText;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Boolean answeredBy(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        Page page = up.page();

        // Use ProductPage helper to detect badge text; poll until timeout to
        // account for rendering delays and minor text variations.
        ProductPage product = new ProductPage(page);
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (product.hasBadgeText(badgeText)) {
                    return true;
                }
                // Fallback: look for any element containing 'agot' (e.g. 'agotarse', 'agotado',
                // 'casi agotado')
                try {
                    if (page.locator("text=/agot/i").first().isVisible()) {
                        return true;
                    }
                } catch (Exception ignoredInner) {
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return false;
    }
}
