package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.pages.CartPage;
import com.example.screenplay.pages.CheckoutPage;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class CheckoutTask implements Task {
    private final String address;
    private final String paymentMethod;

    public CheckoutTask(String address, String paymentMethod) {
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    @Override
    @Step("Realizar checkout con dirección: {address}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        CartPage cart = new CartPage(up.page());
        CheckoutPage checkout = new CheckoutPage(up.page());
        // Execute checkout flow using Allure @Step methods so steps and attachments
        // appear correctly
        Allure.step("Realizar checkout con dirección: " + this.address, () -> {
            openCart(cart, up, actor);
            proceedToCheckout(cart, up, actor);
            fillAddress(checkout, this.address, up, actor);
            selectPaymentMethod(checkout, this.paymentMethod, up, actor);
            confirmPurchase(checkout, up, actor);
        });
    }

    @Step("Abrir carrito")
    private void openCart(CartPage cart, UsePlaywright up, Actor actor) {
        cart.openCart();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Proceder al checkout")
    private void proceedToCheckout(CartPage cart, UsePlaywright up, Actor actor) {
        cart.proceedToCheckout();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Rellenar dirección: {address}")
    private void fillAddress(CheckoutPage checkout, String address, UsePlaywright up, Actor actor) {
        checkout.waitForAddressAndFill(address);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Seleccionar método de pago: {method}")
    private void selectPaymentMethod(CheckoutPage checkout, String method, UsePlaywright up, Actor actor) {
        checkout.selectPaymentMethod(method);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Confirmar compra")
    private void confirmPurchase(CheckoutPage checkout, UsePlaywright up, Actor actor) {
        checkout.confirmPurchase();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }
}
