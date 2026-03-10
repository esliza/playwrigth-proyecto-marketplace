package com.example.screenplay.interactions;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TakeScreenshot implements Task {
    private final String name;

    public TakeScreenshot(String name) {
        this.name = name;
    }

    @Override
    @Step("Captura de pantalla")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        Allure.step("Captura de pantalla", () -> attach(up, actor));
    }

    public static void attach(UsePlaywright up, Actor actor) {
        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String name = (actor != null) ? actor.getName() : "actor";
            String fileName = name + "-screenshot-" + System.currentTimeMillis() + ".png";
            Path out = dir.resolve(fileName);
            byte[] img = up.page().screenshot();
            if (img != null && img.length > 0) {
                try {
                    Files.write(out, img);
                } catch (Exception ignored) {
                }
                try (ByteArrayInputStream is = new ByteArrayInputStream(img)) {
                    Allure.addAttachment("📷 Captura de pantalla", "image/png", is, ".png");
                }
            }
        } catch (Exception e) {
            System.out.println("[TakeScreenshot] failed: " + e.getMessage());
        }
    }
}
