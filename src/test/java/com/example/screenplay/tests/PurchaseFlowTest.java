package com.example.screenplay.tests;

import com.example.screenplay.interactions.Click;
import com.example.screenplay.interactions.EnterText;
import com.example.screenplay.questions.PageContainsText;
import com.example.screenplay.questions.ProductHasBadge;
import com.example.screenplay.questions.ToastMessage;
import com.example.screenplay.tasks.PurchaseTask;
import com.example.screenplay.tasks.LoginTask;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.tasks.PurchaseBySearchTask;
import com.example.screenplay.tasks.PurchaseProductAndServiceTask;
import com.example.screenplay.pages.SearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import com.example.screenplay.utils.CsvDataReader;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

public class PurchaseFlowTest extends BaseTest {

    @BeforeEach
    public void ensureLoggedIn() throws Exception {
        // No reutilizamos una prueba por otra. En vez de eso usamos la Task de login
        // para asegurar que el Actor está autenticado antes de cada caso.
        estefany.attemptsTo(new LoginTask("mairafe@gmail.com", "Password123!"));
        // Verificamos que el login fue exitoso leyendo el toast
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Sesión iniciada"), "Login previo falló: " + toast);
    }

    // Caso 1: Añade 1 producto al carrito (stock disponible) y realiza checkout.
    @ParameterizedTest(name = "addOneProductAndCheckout[{index}] - {0}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#purchaseAddOneProductData")
    @Tag("addOneProductAndCheckout")
    @Epic("Purchasing")
    @Feature("Checkout")
    @Story("Añadir producto y completar checkout")
    public void addOneProductAndCheckout(String searchQuery, String productText, String address, String paymentMethod,
            String expected, String expectedMessage) throws Exception {
        // Ejecuta la misma lógica que antes, pero usando los parámetros leídos del CSV
        estefany.attemptsTo(new PurchaseBySearchTask(searchQuery,
                productText,
                address,
                paymentMethod));

        // Verificar toast final
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Compra realizada con éxito") || toast.toLowerCase().contains("compra"),
                "Se esperaba toast de éxito, se obtuvo: " + toast);
    }

    // Caso 2: Añade un producto que está por agotarse y realiza checkout.
    @ParameterizedTest(name = "addNearOutOfStockProductAndCheckout[{index}] - {0}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#purchaseAddNearOutOfStockData")
    @Tag("addNearOutOfStockProductAndCheckout")
    @Epic("Purchasing")
    @Feature("Checkout")
    @Story("Comprar producto por agotarse")
    public void addNearOutOfStockProductAndCheckout(String productUrl, String badgeText, String address,
            String paymentMethod, String expected, String expectedMessage) throws Exception {
        UsePlaywright up = estefany.abilityTo(UsePlaywright.class);

        // Ir directamente al detalle del producto (desde CSV)
        SearchPage search = new SearchPage(up.page());
        search.openResultById(productUrl);

        // Verificar que el badge esperado esté presente en el detalle
        boolean hasBadge = new ProductHasBadge(badgeText, 5000).answeredBy(estefany);
        assertTrue(hasBadge, "Se esperaba badge '" + badgeText + "' en el detalle del producto");

        // Ejecutar la compra con los datos del CSV
        estefany.attemptsTo(new PurchaseTask(address, paymentMethod));

        // Verificar toast final
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Compra realizada con éxito") || toast.toLowerCase().contains("compra"),
                "Se esperaba toast de éxito, se obtuvo: " + toast);
    }

    // Caso 3: Añade servicio + producto al carrito y realiza checkout.
    @ParameterizedTest(name = "addServiceAndProductAndCheckout[{index}] - {0} + {2}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#purchaseAddServiceAndProductData")
    @Tag("addServiceAndProductAndCheckout")
    @Epic("Purchasing")
    @Feature("Checkout")
    @Story("Añadir servicio y producto y completar checkout")
    public void addServiceAndProductAndCheckout(String productQuery, String productText, String serviceQuery,
            String serviceText, String address, String paymentMethod, String expected, String expectedMessage)
            throws Exception {
        estefany.attemptsTo(new PurchaseProductAndServiceTask(
                productQuery,
                productText,
                serviceQuery,
                serviceText,
                address,
                paymentMethod));

        // Verificar toast final
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Compra realizada con éxito") || toast.toLowerCase().contains("compra"),
                "Se esperaba toast de éxito, se obtuvo: " + toast);
    }

    // Caso 4: Buscar un producto específico, añadirlo, aumentar unidades, vaciar
    // carrito y verificar toast "carrito vacío". Ahora data-driven desde CSV.
    @ParameterizedTest(name = "addFiveProductsAndEmptyCart[{index}] - {0} x{1}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#purchaseAddFiveProductsData")
    @Tag("addFiveProductsAndEmptyCart")
    @Epic("Purchasing")
    @Feature("Cart Management")
    @Story("Añadir múltiples productos y vaciar carrito")
    public void addFiveProductsAndEmptyCart(String productIdentifier, int quantity, String expected,
            String expectedMessage) throws Exception {
        estefany.attemptsTo(new com.example.screenplay.tasks.PurchaseAddAndEmptyTask(
                productIdentifier,
                quantity));
        String toast = new ToastMessage(7000).answeredBy(estefany);
        if (toast == null || !toast.toLowerCase().contains("carrito")) {
            throw new AssertionError("Se esperaba toast indicando carrito vacío, se obtuvo: " + toast);
        }
    }

}
