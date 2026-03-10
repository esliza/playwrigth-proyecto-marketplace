package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.pages.SearchPage;
import com.example.screenplay.pages.ProductPage;
import com.example.screenplay.pages.CartPage;
import com.example.screenplay.pages.CheckoutPage;
import io.qameta.allure.Step;
import io.qameta.allure.Allure;

public class PurchaseBySearchTask implements Task {
    private final String searchQuery;
    private final String productText;
    private final String address;
    private final String paymentMethod;

    public PurchaseBySearchTask(String searchQuery, String productText, String address, String paymentMethod) {
        this.searchQuery = searchQuery;
        this.productText = productText;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    @Override

    @Step("Comprar por búsqueda: {searchQuery}")
    public void performAs(Actor actor) throws Exception {
        // Ejecutar pasos estructurados; las tareas internas adjuntan capturas
        Allure.step("Comprar por búsqueda: " + this.searchQuery, () -> {
            searchAndAdd(actor, this.searchQuery, this.productText);
            performCheckout(actor, this.address, this.paymentMethod);
        });
    }

    @Step("Buscar y añadir producto: {searchQuery}")
    private void searchAndAdd(Actor actor, String searchQuery, String productText) throws Exception {
        actor.attemptsTo(new com.example.screenplay.tasks.SearchAndAddTask(searchQuery, productText));
    }

    @Step("Realizar checkout (dirección={address}, método={paymentMethod})")
    private void performCheckout(Actor actor, String address, String paymentMethod) throws Exception {
        actor.attemptsTo(new com.example.screenplay.tasks.CheckoutTask(address, paymentMethod));
    }
}
