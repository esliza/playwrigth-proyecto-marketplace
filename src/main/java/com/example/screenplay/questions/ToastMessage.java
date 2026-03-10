package com.example.screenplay.questions;

import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Question;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class ToastMessage implements Question<String> {
    private final int timeoutMs;

    public ToastMessage() {
        this(5000);
    }

    public ToastMessage(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public String answeredBy(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        Page page = up.page();
        // Selector targets the toast manager and its first child toast
        Locator toastLocator = page.locator("div[aria-label=\"Notifications-top\"] div").first();
        // wait for visible
        toastLocator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout((double) timeoutMs));
        String text = toastLocator.innerText();
        return text != null ? text.trim() : "";
    }
}
