package com.example.screenplay.tests;

import com.example.screenplay.tasks.RegisterTask;
import com.example.screenplay.questions.ToastMessage;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

/**
 * Tests de registro (data-driven) usando `register-data.csv`.
 *
 * Formato CSV esperado:
 * name,email,telefono,password,confirmPassword,expected,expectedMessage
 * - `email` puede contener `{timestamp}` que se reemplaza en tiempo de
 * ejecución.
 * - `expected` controla si la fila es de tipo "success" o "failure".
 * - `expectedMessage` es el texto que se espera encontrar en el toast
 * resultante.
 */
public class RegisterTest extends BaseTest {

        /**
         * Test parametrizado para registros exitosos.
         * - Consume `CsvDataReader#registerSuccessData` (filtra `expected=success`).
         * - Reemplaza `{timestamp}` en el email para evitar duplicados.
         * - Ejecuta `RegisterTask` y valida que el toast contenga `expectedMessage`.
         */
        @ParameterizedTest(name = "registerSuccess[{index}] - {1}")
        @MethodSource("com.example.screenplay.utils.CsvDataReader#registerSuccessData")
        @Tag("registerSuccess")
        @Epic("Authentication")
        @Feature("Register")
        @Story("Registro con datos válidos (data-driven)")
        public void registerSuccess(String name, String emailTemplate, String telefono, String password,
                        String confirmPassword, String expected, String expectedMessage) throws Exception {
                String email = processEmailTemplate(emailTemplate);
                // Ejecuta la Task de registro sin modificar su implementación
                estefany.attemptsTo(new RegisterTask(name, email, telefono, password, confirmPassword));
                // Verifica el toast devuelto por la aplicación
                String toast = new ToastMessage(7000).answeredBy(estefany);
                assertTrue(toast.contains(expectedMessage), "Expected success toast but got: " + toast);
        }

        /**
         * Test parametrizado para registros que deben fallar.
         * - Consume `CsvDataReader#registerFailureData` (filtra `expected=failure`).
         * - Si el fallo esperado es por correo duplicado, el test crea primero el
         * usuario
         * para garantizar la condición de duplicidad antes de reintentar el registro.
         */
        @ParameterizedTest(name = "registerFailure[{index}] - {1}")
        @MethodSource("com.example.screenplay.utils.CsvDataReader#registerFailureData")
        @Tag("registerFailure")
        @Epic("Authentication")
        @Feature("Register")
        @Story("Registro fallido (data-driven)")
        public void registerFailure(String name, String emailTemplate, String telefono, String password,
                        String confirmPassword, String expected, String expectedMessage) throws Exception {
                String email = processEmailTemplate(emailTemplate);
                // Si el fallo esperado es por email duplicado, pre-creamos el usuario
                if (expectedMessage != null && expectedMessage.contains("correo")
                                && expectedMessage.contains("registrado")) {
                        estefany.attemptsTo(new RegisterTask(name, email, telefono, password, confirmPassword));
                        String firstToast = new ToastMessage(7000).answeredBy(estefany);
                        assertTrue(firstToast.contains("Registro exitoso"),
                                        "Expected initial success but got: " + firstToast);
                }

                // Intento que debe fallar según el CSV
                estefany.attemptsTo(new RegisterTask(name, email, telefono, password, confirmPassword));
                String toast = new ToastMessage(7000).answeredBy(estefany);
                assertTrue(toast.contains(expectedMessage), "Expected failure toast but got: " + toast);
        }

        // Reemplaza `{timestamp}` en la plantilla de email si está presente
        private String processEmailTemplate(String template) {
                if (template == null)
                        return null;
                if (template.contains("{timestamp}")) {
                        return template.replace("{timestamp}", String.valueOf(System.currentTimeMillis()));
                }
                return template;
        }
}
