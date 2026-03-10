package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.interactions.Click;
import com.example.screenplay.pages.ProductPage;
import com.example.screenplay.pages.CartPage;
import com.example.screenplay.pages.CheckoutPage;
import com.example.screenplay.abilities.UsePlaywright;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class PurchaseTask implements Task {
    private final String address;
    private final String paymentMethod;

    public PurchaseTask(String address, String paymentMethod) {
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    @Override
    @Step("Realizar compra con dirección: {address} y pago: {paymentMethod}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);

        ProductPage product = new ProductPage(up.page());
        CartPage cart = new CartPage(up.page());
        CheckoutPage checkout = new CheckoutPage(up.page());

        Allure.step("Realizar compra con dirección: " + this.address + " y pago: " + this.paymentMethod, () -> {
            addProductToCart(product, up, actor);
            openCartAndProceed(cart, up, actor);
            fillAddressAndComplete(checkout, up, actor);
            confirmPurchase(checkout, up, actor);
        });
    }

    @Step("Añadir producto al carrito")
    private void addProductToCart(ProductPage product, UsePlaywright up, Actor actor) {
        product.addToCart();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Abrir carrito y proceder al checkout")
    private void openCartAndProceed(CartPage cart, UsePlaywright up, Actor actor) {
        cart.openCart();
        cart.proceedToCheckout();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Rellenar dirección")
    private void fillAddressAndComplete(CheckoutPage checkout, UsePlaywright up, Actor actor) {
        checkout.waitForAddressAndFill(this.address);
        checkout.selectPaymentMethod(this.paymentMethod);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Confirmar compra")
    private void confirmPurchase(CheckoutPage checkout, UsePlaywright up, Actor actor) {
        checkout.confirmPurchase();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

}
