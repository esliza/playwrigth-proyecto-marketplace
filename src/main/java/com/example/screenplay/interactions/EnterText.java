package com.example.screenplay.interactions;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Interaction;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;

public class EnterText implements Interaction {
    private final String selector;
    private final String text;

    public EnterText(String selector, String text) {
        this.selector = selector;
        this.text = text;
    }

    @Override
    @Step("Introduzca el texto '{text}' en {selector}")
    public void performAs(Actor actor) {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        Allure.step("Introduzca el texto '" + text + "' en " + selector, () -> {
            up.page().fill(selector, text);
            try {
                // Attach screenshot inside this step
                TakeScreenshot.attach(up, actor);
            } catch (Exception ignored) {
            }
        });
    }
}
