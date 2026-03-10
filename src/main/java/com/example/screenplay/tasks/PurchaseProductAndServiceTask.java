package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.pages.SearchPage;
import com.example.screenplay.pages.ProductPage;
import com.example.screenplay.pages.CartPage;
import com.example.screenplay.pages.CheckoutPage;
import io.qameta.allure.Step;

public class PurchaseProductAndServiceTask implements Task {
    private final String productQuery;
    private final String productText;
    private final String serviceQuery;
    private final String serviceText;
    private final String address;
    private final String paymentMethod;

    public PurchaseProductAndServiceTask(String productQuery, String productText,
            String serviceQuery, String serviceText,
            String address, String paymentMethod) {
        this.productQuery = productQuery;
        this.productText = productText;
        this.serviceQuery = serviceQuery;
        this.serviceText = serviceText;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    @Override
    @Step("Comprar producto y servicio: {productQuery} + {serviceQuery}")
    public void performAs(Actor actor) throws Exception {
        // Ejecutar pasos estructurados; las tareas internas adjuntan capturas
        addProduct(actor, this.productQuery, this.productText);
        addService(actor, this.serviceQuery, this.serviceText);
        performCheckout(actor, this.address, this.paymentMethod);
    }

    @Step("Añadir producto: {searchQuery}")
    private void addProduct(Actor actor, String searchQuery, String productText) throws Exception {
        actor.attemptsTo(new com.example.screenplay.tasks.SearchAndAddTask(searchQuery, productText));
    }

    @Step("Añadir servicio: {searchQuery}")
    private void addService(Actor actor, String searchQuery, String serviceText) throws Exception {
        actor.attemptsTo(new com.example.screenplay.tasks.SearchAndAddTask(searchQuery, serviceText));
    }

    @Step("Realizar checkout (dirección={address}, método={paymentMethod})")
    private void performCheckout(Actor actor, String address, String paymentMethod) throws Exception {
        actor.attemptsTo(new com.example.screenplay.tasks.CheckoutTask(address, paymentMethod));
    }

}
