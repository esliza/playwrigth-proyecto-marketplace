package com.example.screenplay.interactions;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Interaction;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;

public class Click implements Interaction {
    private final String selector;

    public Click(String selector) {
        this.selector = selector;
    }

    @Override
    @Step("Haga clic en el elemento {selector}")
    public void performAs(Actor actor) {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        Allure.step("Haga clic en el elemento " + selector, () -> {
            up.page().click(selector);
            try {
                // Attach screenshot from inside the step to have it nested under this step
                TakeScreenshot.attach(up, actor);
            } catch (Exception ignored) {
            }
        });
    }
}
