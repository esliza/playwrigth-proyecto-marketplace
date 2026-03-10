package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.pages.SearchPage;
import com.example.screenplay.pages.ProductPage;
import com.example.screenplay.pages.CartPage;
import com.example.screenplay.questions.ToastMessage;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class PurchaseAddAndEmptyTask implements Task {
    private final String productQuery;
    private final int cantidad;

    public PurchaseAddAndEmptyTask(String productQuery, int cantidad) {
        this.productQuery = productQuery;
        this.cantidad = cantidad;
    }

    @Override
    @Step("Comprar, añadir y vaciar: {productQuery}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);

        // Execute structured steps with screenshots
        // perform the SearchAndAdd task as an actor-level task (it will attach
        // screenshots within its steps)
        Allure.step("Comprar, añadir y vaciar: " + this.productQuery, () -> {
            actor.attemptsTo(new com.example.screenplay.tasks.SearchAndAddTask(this.productQuery, this.productQuery));

            CartPage cart = new CartPage(up.page());
            openCart(cart, up, actor);

            increaseQuantity(cart, this.cantidad, up, actor);

            emptyCart(cart, up, actor);
        });

    }

    @Step("Abrir carrito")
    private void openCart(CartPage cart, UsePlaywright up, Actor actor) {
        cart.openCart();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Aumentar cantidad a {cantidad}")
    private void increaseQuantity(CartPage cart, int cantidad, UsePlaywright up, Actor actor) {
        if (cantidad > 1) {
            cart.increaseQuantity(cantidad - 1);
        }
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Vaciar carrito")
    private void emptyCart(CartPage cart, UsePlaywright up, Actor actor) {
        cart.emptyCart();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }
}
