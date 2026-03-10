**Guía de Buenas Prácticas: Screenplay + Java + Playwright**

Resumen
- Propósito: recomendaciones prácticas para mantener y escalar un proyecto de automatización usando el patrón Screenplay en Java con Playwright.
- Público: desarrolladores/QA que migran desde Selenium/Cucumber/Serenity o que comienzan con Playwright.

**Estructura del Proyecto (sugerida)**
- src/main/java/com/example/screenplay/
  - abilities/         : habilidades del Actor (UsePlaywright, UseApi, etc.)
  - interactions/      : interacciones pequeñas (Click, EnterText, WaitFor)
  - tasks/             : tareas compuestas por Interactions (SearchAndAddTask, CheckoutTask)
  - questions/         : consultas/expectativas sobre el sistema (ToastMessage, PageHasText)
  - pages/             : objetos ligeros con selectores y helpers (no lógica de negocio)
  - utils/             : utilidades (selectors, retries, test data)
- src/test/java/...     : tests (clases JUnit que orquestan Actors y Tasks)
- docs/                : guías, checklist, runbooks

¿Por qué esta separación?
- Mantiene Tasks enfocadas en comportamiento, Interactions en operaciones atómicas y Pages sólo en selectores.
- Facilita mantenimiento y testabilidad.

**Convenciones y Estilo**
- Nombres: `SearchPage`, `AddToCartTask`, `FillAddressInteraction`, `ToastMessage`.
- Paquetes claros: `com.example.screenplay.tasks` etc.
- Usar constantes para rutas/URLs en `config` o `Env`.
- Evitar selectores frágiles (XPATHs largos, texto visible) — preferir `data-test`.

**Screenplay: buenas prácticas**
- Tasks pequeñas y reutilizables: Compose tasks más complejas a partir de tasks más pequeñas.
- Mantén la verificación fuera de Tasks: Tasks realizan acciones; Questions y asserts en Tests.
- Interactions atómicas: `Click`, `Fill`, `WaitUntilVisible`.
- Abilities: encapsula Playwright en `UsePlaywright` que administra ciclo de vida (browser/context/page) y configuración por propiedades (`playwright.headless`, `playwright.slowMo`).
- Page objects minimalistas: métodos para acciones simples; no incorporar asserts.

**Selectors y estabilidad**
- Añadir `data-test` o `data-qa` en la app para elementos clave.
- Evitar `:has-text` como primer intento; usar id/data-test/aria-label.
- Centralizar selectores en `pages/` para facilitar cambios.

**Manejo de esperas y flakiness**
- Aprovecha auto-waiting de Playwright pero añade `waitForSelector` antes de acciones críticas cuando la app navega o carga datos asíncronos.
- Implementa retries controlados a nivel de Interaction (no infinitos).
- Usa configuraciones de tiempo más largas en CI (p. ej. aumentar timeouts para slow CI machines).
- Captura traces/videos/screenshots en fallos (configurable por entorno).

**Test data y entornos**
- Evita cargar datos por UI cuando sea posible: usa API fixtures para preparar estado (Abilities para HTTP clients).
- Mantén fixtures idempotentes y limpiables.
- Separar envs: `test`, `staging`, `local` con configuración en `resources/config.properties` o variables de entorno.

**CI / Ejecución**
- Ejecutar tests en paralelo por clase o por método con Maven Surefire/Failsafe configurado.
- Habilitar `playwright` browsers en agentes CI o usar contenedores Docker que incluyan navegadores.
- Guardar artefactos en cada ejecución (videos, traces, screenshots, logs).
- Configurar reportes (Allure, JUnit XML) y visualizar fallos con artefactos.

**Observabilidad y debugging**
- Habilitar `video` y `trace` condicionalmente (solo en falla o cuando `playwright.record=true`).
- Registrar pasos importantes en logs (Actor.attemptsTo) — usa un logger estructurado.
- Añadir `slowMo` y `headed` flags para reproducciones locales.

**Arquitectura recomendada de Tests**
- Tests JUnit deben orquestar: crear Actor con Ability, ejecutar Tasks, usar Questions para asserts.
- No mezclar lógica UI en tests; los tests deben leer como especificaciones:
  estefany.attemptsTo(new LoginTask(...));
  assertTrue(new ToastMessage().answeredBy(estefany).contains("Sesión iniciada"));

**Playwright (Java) — prácticas específicas**
- Reutiliza BrowserContext para aislar test cases (un Context por test). Cierra contextos al terminar.
- Usa `page.waitForLoadState()` cuando necesites asegurar carga completa.
- Usa `page.locator(selector)` para obtener `Locator` y luego `waitFor()`/`click()`/`fill()`.
- Usa `page.pause()` y `slowMo` en desarrollo local para ver reproducciones.

**Comparación breve: Selenium vs Playwright**
- Arquitectura:
  - Selenium: cliente-servidor (WebDriver) con drivers por navegador. Playwright: controlador directo con comunicación por protocolo, mantiene sus propios bins.
- Lenguajes:
  - Selenium: multi-language madura (Java, Python, JS, C#...). Playwright: soporta JS/TS, Python, Java, .NET.
- Auto-waiting y fiabilidad:
  - Selenium: no auto-waiting por defecto; requiere explícitos `WebDriverWait`. Más propenso a flakiness.
  - Playwright: espera automática para acciones, menos flacky y más rápido en muchas operaciones.
- Soporte multi-page / frames / popups:
  - Playwright: manejo nativo y sencillo de múltiples páginas y contextos.
  - Selenium: manejo posible pero más verboso.
- Network & intercept:
  - Playwright: interceptación y mocking de red integrada (muy útil para testing de bordes y fixtures).
  - Selenium: no ofrece intercept nativo (requiere proxy externo o herramientas adicionales).
- Performance:
  - Playwright suele ser más rápido y estable en operaciones UI gracias a su diseño.
- Ecosistema / herramientas:
  - Selenium: amplio ecosistema (Serenity, Cucumber, Grid). Playwright: solido pero más nuevo; Playwright Test (JS) tiene features avanzadas (tracing, retries) — algunas no están en Java SDK.

**Migración desde Selenium+Gherkin**
- Mantener el patrón Screenplay y Tasks; reemplazar Abilities/Interactions para Playwright.
- Revisar fixtures y mocks: con Playwright puedes usar network interception para estabilizar escenarios.
- Ajustar timeouts y aprovechar auto-wait; reducir sleeps.

**Checklist rápido para escalabilidad**
- [ ] `data-test` en elementos críticos
- [ ] Abilities bien definidos (`UsePlaywright`, `UseApi`)
- [ ] Tasks pequeñas y composables
- [ ] Questions para todas las aserciones
- [ ] Centralizar selectores en `pages/`
- [ ] Captura de artefactos (video/trace/screenshots)
- [ ] Tests idempotentes y con preparación por API cuando sea posible
- [ ] CI con agentes que soporten navegadores y artefactos

**Conclusión y próximos pasos concretos**
- Añadir `data-test` en la app es la inversión de mayor retorno.
- Extraer y centralizar `SearchAndAddTask` y `CheckoutTask` (ya lo iniciamos) reducirá duplicación.
- Implementar `Ability` para API y fixtures permitirá tests más rápidos y robustos.
- Configurar CI para ejecutar tests en paralelo y recopilar artefactos.

Referencias rápidas
- Playwright Java: https://playwright.dev/java/
- Playwright concepts: locators, auto-wait, tracing, video

---
Archivo generado: docs/BEST_PRACTICES_PLAYWRIGHT_SCREENPLAY.md
