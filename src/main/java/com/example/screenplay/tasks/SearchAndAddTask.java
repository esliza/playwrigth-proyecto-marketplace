package com.example.screenplay.tasks;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.core.Task;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.pages.SearchPage;
import com.example.screenplay.pages.ProductPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

import java.io.ByteArrayInputStream;

public class SearchAndAddTask implements Task {
    private final String query;
    private final String resultIdOrText; // flexible: id / partial href / visible text

    public SearchAndAddTask(String query, String resultIdOrText) {
        this.query = query;
        this.resultIdOrText = resultIdOrText;
    }

    @Override
    @Step("Buscar y añadir producto: {query}")
    public void performAs(Actor actor) throws Exception {
        UsePlaywright up = actor.abilityTo(UsePlaywright.class);
        SearchPage search = new SearchPage(up.page());
        ProductPage product = new ProductPage(up.page());
        Allure.step("Buscar y añadir producto: " + this.query, () -> {
            openHome(search, up, actor);

            searchFor(this.query, search, up, actor);

            openResult(search, this.resultIdOrText, up, actor);

            addProductToCart(product, up, actor);
        });
    }

    @Step("Abrir página principal de búsqueda")
    private void openHome(SearchPage search, UsePlaywright up, Actor actor) {
        search.openHome();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Buscar: {q}")
    private void searchFor(String q, SearchPage search, UsePlaywright up, Actor actor) {
        search.search(q);
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Abrir resultado de búsqueda: {idOrText}")
    private void openResult(SearchPage search, String idOrText, UsePlaywright up, Actor actor) {
        try {
            search.openResultById(idOrText);
        } catch (Exception e) {
            search.openResultByText(idOrText);
        }
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }

    @Step("Añadir producto al carrito")
    private void addProductToCart(ProductPage product, UsePlaywright up, Actor actor) {
        product.addToCart();
        com.example.screenplay.interactions.TakeScreenshot.attach(up, actor);
    }
}
