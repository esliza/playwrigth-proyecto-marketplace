package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.pages.RegisterPage;
import com.example.screenplay.pages.HomePage;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class RegisterTask implements Task {
    private final String name;
    private final String email;
    private final String telefono;
    private final String password;
    private final String confirmPassword;

    public RegisterTask(String name, String email, String telefono, String password, String confirmPassword) {
        this.name = name;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    @Override
    @Step("Registrar usuario: {email}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        com.example.screenplay.pages.HomePage home = new com.example.screenplay.pages.HomePage(up.page());
        Allure.step("Registrar usuario: " + email, () -> {
            openRegistration(home, up, actor);

            RegisterPage register = new RegisterPage(up.page());
            fillRegistrationForm(register, up, actor);
            submitRegistration(register, up, actor);
        });
    }

    @Step("Abrir formulario de registro")
    private void openRegistration(HomePage home, UsePlaywright up, Actor actor) {
        home.open();
        home.clickRegister();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Llenar formulario de registro para: {email}")
    private void fillRegistrationForm(RegisterPage register, UsePlaywright up, Actor actor) {
        register.fillName(name);
        register.fillEmail(email);
        register.fillPhone(telefono);
        register.fillPassword(password);
        register.fillConfirmPassword(confirmPassword);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Enviar registro")
    private void submitRegistration(RegisterPage register, UsePlaywright up, Actor actor) {
        register.submit();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }
}
