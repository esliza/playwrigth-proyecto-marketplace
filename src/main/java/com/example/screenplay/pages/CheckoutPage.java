package com.example.screenplay.pages;

import com.microsoft.playwright.Page;

public class CheckoutPage {
    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public void waitForAddressAndFill(String address) {
        String[] addressSelectors = new String[] {
                "textarea[id='field-:rt:']",
                "textarea[placeholder*='Ej: Calle']",
                "textarea"
        };
        String chosen = null;
        for (String sel : addressSelectors) {
            try {
                page.waitForSelector(sel, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(2500));
                chosen = sel;
                break;
            } catch (Exception ignored) {
            }
        }
        if (chosen == null)
            chosen = "textarea";
        page.fill(chosen, address);
    }

    public void selectPaymentMethod(String paymentMethod) {
        String[] paymentInputSelectors = new String[] {
                "input[placeholder*='Ej: Efectivo, Transferencia, Tarjeta']",
                "input[aria-label*='Método']",
                "input[name*='payment']",
                "input[id='field-:rf:']",
                "input"
        };
        String chosen = null;
        for (String sel : paymentInputSelectors) {
            try {
                page.waitForSelector(sel, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(2000));
                chosen = sel;
                break;
            } catch (Exception ignored) {
            }
        }
        if (chosen != null) {
            try {
                page.fill(chosen, paymentMethod);
            } catch (Exception ex) {
                try {
                    page.click("text=" + paymentMethod);
                } catch (Exception ignored) {
                }
            }
        } else {
            try {
                page.click("text=" + paymentMethod);
            } catch (Exception ignored) {
            }
        }
    }

    public void confirmPurchase() {
        try {
            page.click("button:has-text(\"Confirmar compra\")");
        } catch (Exception e) {
            try {
                page.click("button[type=submit]");
            } catch (Exception ignored) {
            }
        }
    }
}
