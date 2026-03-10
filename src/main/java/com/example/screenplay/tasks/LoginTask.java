package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.pages.LoginPage;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class LoginTask implements Task {
    private final String email;
    private final String password;

    public LoginTask(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    @Step("Iniciar sesión con {email}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        LoginPage page = new LoginPage(up.page());
        Allure.step("Iniciar sesión con " + email, () -> {
            openLogin(page, up, actor);
            fillCredentials(page, up, actor);
            submit(page, up, actor);
        });
    }

    @Step("Abrir página de login {page.url}")
    private void openLogin(com.example.screenplay.pages.LoginPage page, UsePlaywright up, Actor actor) {
        page.open();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Llenar credenciales: {email}")
    private void fillCredentials(com.example.screenplay.pages.LoginPage page, UsePlaywright up, Actor actor)
            throws Exception {
        page.fillEmail(email);
        page.fillPassword(password);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Enviar formulario de login")
    private void submit(com.example.screenplay.pages.LoginPage page, UsePlaywright up, Actor actor) throws Exception {
        page.submit();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }
}
