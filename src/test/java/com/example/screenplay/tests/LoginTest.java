package com.example.screenplay.tests;

import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.questions.PageContainsText;
import com.example.screenplay.questions.ToastMessage;
import com.example.screenplay.tasks.LoginTask;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

public class LoginTest extends BaseTest {

    /**
     * Tests de login (data-driven).
     *
     * - Los datos se leen desde `src/test/resources/testdata/login-data.csv`
     * mediante
     * `CsvDataReader#loginSuccessData`, `#loginFailureData` o `#loginAllData`.
     * - El `@MethodSource` inyecta los parámetros del CSV en el método de prueba.
     * - El test lanza `LoginTask` (no cambiar la Task aquí) y luego verifica el
     * toast
     * devuelto por la aplicación.
     */

    @ParameterizedTest(name = "loginSuccess[{index}] - {0}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#loginSuccessData")
    @Tag("loginSuccess")
    @Epic("Authentication")
    @Feature("Login")
    @Story("Login con credenciales válidas (data-driven)")
    public void loginSuccess(String email, String password) throws Exception {
        estefany.attemptsTo(new LoginTask(email, password));
        // Wait for toast and assert success text
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Sesión iniciada"), "Expected success toast but got: " + toast);
    }

    /**
     * Test parametrizado que separa ejecuciones de éxito y fallo mediante
     * dos MethodSource diferentes que filtran la columna `expected` del CSV.
     *
     * Parámetros: (email, password)
     * - `loginSuccess` valida que aparezca el mensaje "Sesión iniciada".
     * - `loginFailure` valida que aparezca el mensaje "Error al iniciar sesión".
     */
    @ParameterizedTest(name = "loginFailure[{index}] - {0}")
    @MethodSource("com.example.screenplay.utils.CsvDataReader#loginFailureData")
    @Tag("loginFailure")
    @Epic("Authentication")
    @Feature("Login")
    @Story("Login con credenciales inválidas (data-driven)")
    public void loginFailure(String email, String password) throws Exception {
        estefany.attemptsTo(new LoginTask(email, password));
        String toast = new ToastMessage(7000).answeredBy(estefany);
        assertTrue(toast.contains("Error al iniciar sesión"), "Expected failure toast but got: " + toast);
    }
}
