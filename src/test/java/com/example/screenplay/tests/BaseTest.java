package com.example.screenplay.tests;

import com.example.screenplay.actors.Actor;
import com.example.screenplay.abilities.UsePlaywright;
import com.example.screenplay.utils.PlaywrightManager;
import io.qameta.allure.Allure;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@ExtendWith(AllureJunit5.class)
public class BaseTest {
    protected Actor estefany;
    protected UsePlaywright usePlaywright;

    @RegisterExtension
    protected TestResultWatcher resultWatcher = new TestResultWatcher();

    @RegisterExtension
    protected static final AllureJunit5 allureExtension = new AllureJunit5();

    // Ensure we clear previous artifacts once per JVM test run
    private static final java.util.concurrent.atomic.AtomicBoolean cleaned = new java.util.concurrent.atomic.AtomicBoolean(
            false);

    @BeforeEach
    public void setUp(org.junit.jupiter.api.TestInfo testInfo) {
        // On first setup in this JVM, clean previous screenshots/videos to keep only
        // current run
        if (cleaned.compareAndSet(false, true)) {
            try {
                // Ensure Allure writes results into target/allure-results for consistency
                System.setProperty("allure.results.directory", Paths.get("target", "allure-results").toString());

                java.nio.file.Path target = java.nio.file.Paths.get("target");
                java.nio.file.Path screenshots = target.resolve("screenshots");
                java.nio.file.Path videos = target.resolve("videos");
                java.nio.file.Path playwrightVideos = target.resolve("playwright-videos");
                java.util.function.Consumer<java.nio.file.Path> deleteRecursively = p -> {
                    try {
                        if (java.nio.file.Files.exists(p)) {
                            java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(p);
                            walk.sorted(java.util.Comparator.reverseOrder()).forEach(q -> {
                                try {
                                    java.nio.file.Files.deleteIfExists(q);
                                } catch (Exception ignored) {
                                }
                            });
                            walk.close();
                        }
                    } catch (Exception ignored) {
                    }
                };
                deleteRecursively.accept(screenshots);
                deleteRecursively.accept(videos);
                deleteRecursively.accept(playwrightVideos);
                // Also remove any existing project-root allure-results to avoid stale files
                try {
                    java.nio.file.Path rootAllure = java.nio.file.Paths.get("allure-results");
                    deleteRecursively.accept(rootAllure);
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        }

        String displayName = (testInfo != null) ? testInfo.getDisplayName() : null;
        // Respect the `playwright.headless` system property instead of forcing headed
        // runs
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        usePlaywright = PlaywrightManager.startChrome(headless, displayName);
        estefany = new Actor("Estefany");
        estefany.can(usePlaywright);
        // use official Allure steps; no StepLogger fallback
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        if (usePlaywright != null) {
            if (!usePlaywright.isHeadless()) {
                try {
                    Thread.sleep(3000); // esperar 3s para ver el estado final
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            usePlaywright.close();
            // mover videos y adjuntar screenshots/videos a Allure para TODAS las
            // ejecuciones
            try {
                String testName = (testInfo != null) ? testInfo.getDisplayName() : estefany.getName();
                java.util.List<java.nio.file.Path> movedVideos = usePlaywright.moveRecordedVideos("target/videos",
                        testName);

                // remove any other video folders to keep only this test's videos
                try {
                    java.nio.file.Path videosRoot = java.nio.file.Paths.get("target", "videos");
                    if (java.nio.file.Files.exists(videosRoot)) {
                        try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(videosRoot)) {
                            s.forEach(p -> {
                                if (!p.getFileName().toString().equals(sanitizeFileName(testName))) {
                                    try {
                                        java.util.stream.Stream<java.nio.file.Path> walk = java.nio.file.Files.walk(p);
                                        walk.sorted(java.util.Comparator.reverseOrder()).forEach(q -> {
                                            try {
                                                java.nio.file.Files.deleteIfExists(q);
                                            } catch (Exception ignored) {
                                            }
                                        });
                                        walk.close();
                                    } catch (Exception ignored) {
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception ignored) {
                }

                // attach videos
                // Attach videos under a dedicated Allure step so they show in the correct place
                // Attach videos moved by UsePlaywright (prefer moved list to avoid races)
                if (movedVideos != null && !movedVideos.isEmpty()) {
                    for (Path p : movedVideos) {
                        try (InputStream is = Files.newInputStream(p)) {
                            Allure.addAttachment("🎥 Vídeo de ejecución", "video/webm", is, ".webm");
                        } catch (Exception ignored) {
                        }
                    }
                } else {
                    Path videosDir = Paths.get("target", "videos", sanitizeFileName(testName));
                    if (Files.exists(videosDir)) {
                        try (java.util.stream.Stream<Path> stream = Files.list(videosDir)) {
                            java.util.List<Path> videoFiles = new java.util.ArrayList<>();
                            stream.forEach(videoFiles::add);
                            if (!videoFiles.isEmpty()) {
                                for (Path p : videoFiles) {
                                    try (InputStream is = Files.newInputStream(p)) {
                                        Allure.addAttachment("🎥 Vídeo de ejecución", "video/webm", is, ".webm");
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }

                // Attach screenshots only when the test failed, and attach them inside a
                // failure-evidence step
                Path screenshotsDir = Paths.get("target", "screenshots");
                if (resultWatcher != null && resultWatcher.isFailed() && Files.exists(screenshotsDir)) {
                    try (java.util.stream.Stream<Path> stream = Files.list(screenshotsDir)) {
                        java.util.List<Path> shotFiles = new java.util.ArrayList<>();
                        stream.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                                .forEach(shotFiles::add);
                        if (!shotFiles.isEmpty()) {
                            for (Path p : shotFiles) {
                                try (InputStream is = Files.newInputStream(p)) {
                                    Allure.addAttachment("📷 Captura de pantalla", "image/png", is, ".png");
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                // Write a small JSON summary for the last run
                try {
                    java.util.Map<String, Object> summary = new java.util.LinkedHashMap<>();
                    summary.put("testName", testName);
                    summary.put("actor", estefany != null ? estefany.getName() : null);
                    summary.put("failed", resultWatcher != null && resultWatcher.isFailed());
                    java.util.List<String> scShots = new java.util.ArrayList<>();
                    if (Files.exists(screenshotsDir)) {
                        try (java.util.stream.Stream<Path> stream = Files.list(screenshotsDir)) {
                            stream.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                                    .forEach(p -> scShots.add(p.toString().replace('\\', '/')));
                        } catch (Exception ignored) {
                        }
                    }
                    summary.put("screenshots", scShots);
                    java.util.List<String> vids = new java.util.ArrayList<>();
                    Path vDir = Paths.get("target", "videos", sanitizeFileName(testName));
                    if (Files.exists(vDir)) {
                        try (java.util.stream.Stream<Path> stream = Files.list(vDir)) {
                            stream.forEach(p -> vids.add(p.toString().replace('\\', '/')));
                        } catch (Exception ignored) {
                        }
                    }
                    summary.put("videos", vids);
                    // Try to enrich summary with Allure result (steps, parameters, attachments)
                    try {
                        Path allureDir = Paths.get("target", "allure-results");
                        if (!Files.exists(allureDir) || !Files.isDirectory(allureDir)) {
                            // Fallback to project-root allure-results (some setups write there)
                            Path alt = Paths.get("allure-results");
                            if (Files.exists(alt) && Files.isDirectory(alt)) {
                                allureDir = alt;
                            }
                        }
                        if (Files.exists(allureDir) && Files.isDirectory(allureDir)) {
                            // find a result json that matches the test name
                            try (java.util.stream.Stream<Path> s = Files.list(allureDir)) {
                                Path matched = null;
                                for (Path p : (Iterable<Path>) s::iterator) {
                                    String fname = p.getFileName().toString();
                                    if (fname.endsWith("-result.json") || fname.endsWith(".json")) {
                                        try {
                                            String content = new String(Files.readAllBytes(p));
                                            JsonObject jo = JsonParser.parseString(content).getAsJsonObject();
                                            String name = jo.has("name") ? jo.get("name").getAsString() : null;
                                            if (name != null) {
                                                // compare normalized names
                                                String norm = testName.replaceAll("\\(\\)\\s*$", "");
                                                if (name.equals(norm) || fname.contains(sanitizeFileName(norm))) {
                                                    matched = p;
                                                    // parse steps and parameters
                                                    JsonArray steps = jo.has("steps") ? jo.getAsJsonArray("steps")
                                                            : null;
                                                    java.util.List<java.util.Map<String, Object>> outSteps = new java.util.ArrayList<>();
                                                    if (steps != null) {
                                                        for (JsonElement se : steps) {
                                                            JsonObject so = se.getAsJsonObject();
                                                            java.util.Map<String, Object> stepMap = new java.util.LinkedHashMap<>();
                                                            stepMap.put("name",
                                                                    so.has("name") ? so.get("name").getAsString()
                                                                            : null);
                                                            stepMap.put("status",
                                                                    so.has("status") ? so.get("status").getAsString()
                                                                            : null);
                                                            java.util.List<String> stepAtt = new java.util.ArrayList<>();
                                                            if (so.has("attachments")) {
                                                                for (JsonElement a : so.getAsJsonArray("attachments")) {
                                                                    JsonObject ao = a.getAsJsonObject();
                                                                    if (ao.has("source"))
                                                                        stepAtt.add(allureDir
                                                                                .resolve(ao.get("source").getAsString())
                                                                                .toString().replace('\\', '/'));
                                                                }
                                                            }
                                                            stepMap.put("attachments", stepAtt);
                                                            outSteps.add(stepMap);
                                                        }
                                                    }
                                                    summary.put("steps", outSteps);
                                                    // parameters
                                                    java.util.Map<String, String> params = new java.util.LinkedHashMap<>();
                                                    if (jo.has("parameters")) {
                                                        for (JsonElement pe : jo.getAsJsonArray("parameters")) {
                                                            JsonObject po = pe.getAsJsonObject();
                                                            String pname = po.has("name") ? po.get("name").getAsString()
                                                                    : null;
                                                            String pval = po.has("value")
                                                                    ? po.get("value").getAsString()
                                                                    : null;
                                                            if (pname != null)
                                                                params.put(pname, pval);
                                                        }
                                                    }
                                                    summary.put("parameters", params);
                                                    // top-level attachments
                                                    java.util.List<String> ar = new java.util.ArrayList<>();
                                                    if (jo.has("attachments")) {
                                                        for (JsonElement ae : jo.getAsJsonArray("attachments")) {
                                                            JsonObject ao = ae.getAsJsonObject();
                                                            if (ao.has("source"))
                                                                ar.add(allureDir.resolve(ao.get("source").getAsString())
                                                                        .toString().replace('\\', '/'));
                                                        }
                                                    }
                                                    // merge with screenshots/videos lists
                                                    for (String a : ar) {
                                                        if (!scShots.contains(a) && !vids.contains(a))
                                                            scShots.add(a);
                                                    }
                                                    summary.put("screenshots", scShots);
                                                    summary.put("videos", vids);
                                                    // If no steps found in allure-results, leave steps empty
                                                    break;
                                                }
                                            }
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    // Write per-test JSON into target/last-run-tests and then regenerate
                    // target/last-run-summary.json aggregating the whole suite.
                    Path testsDir = Paths.get("target", "last-run-tests");
                    if (!Files.exists(testsDir))
                        Files.createDirectories(testsDir);
                    String fileName = sanitizeFileName(testName) + ".json";
                    Path single = testsDir.resolve(fileName);
                    try (java.io.Writer w = Files.newBufferedWriter(single)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append('{');
                        sb.append("\"testName\":").append(jsonEscape(summary.get("testName"))).append(',');
                        sb.append("\"actor\":").append(jsonEscape(summary.get("actor"))).append(',');
                        sb.append("\"failed\":").append(summary.get("failed")).append(',');
                        sb.append("\"screenshots\":").append(listToJsonArray(scShots)).append(',');
                        sb.append("\"videos\":").append(listToJsonArray(vids)).append(',');
                        // steps
                        Object stepsObj = summary.get("steps");
                        if (stepsObj instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<java.util.Map<String, Object>> stepsList = (java.util.List<java.util.Map<String, Object>>) stepsObj;
                            sb.append("\"steps\":");
                            sb.append('[');
                            boolean f = true;
                            for (java.util.Map<String, Object> sm : stepsList) {
                                if (!f)
                                    sb.append(',');
                                else
                                    f = false;
                                sb.append('{');
                                sb.append("\"name\":").append(jsonEscape(sm.get("name"))).append(',');
                                sb.append("\"status\":").append(jsonEscape(sm.get("status"))).append(',');
                                @SuppressWarnings("unchecked")
                                java.util.List<String> at = (java.util.List<String>) sm.get("attachments");
                                sb.append("\"attachments\":").append(listToJsonArray(at));
                                sb.append('}');
                            }
                            sb.append(']').append(',');
                        }
                        // parameters
                        Object paramsObj = summary.get("parameters");
                        if (paramsObj instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, String> params = (java.util.Map<String, String>) paramsObj;
                            sb.append("\"parameters\":{");
                            boolean fp = true;
                            for (java.util.Map.Entry<String, String> e : params.entrySet()) {
                                if (!fp)
                                    sb.append(',');
                                else
                                    fp = false;
                                sb.append(jsonEscape(e.getKey())).append(':').append(jsonEscape(e.getValue()));
                            }
                            sb.append('}');
                        } else {
                            // remove trailing comma if present
                            int len = sb.length();
                            if (len > 0 && sb.charAt(len - 1) == ',')
                                sb.setLength(len - 1);
                        }
                        sb.append('}');
                        w.write(sb.toString());
                    }

                    // Regenerate aggregate summary
                    try {
                        java.util.List<String> parts = new java.util.ArrayList<>();
                        try (java.util.stream.Stream<Path> s = Files.list(testsDir)) {
                            s.forEach(p -> {
                                try {
                                    String c = new String(Files.readAllBytes(p));
                                    parts.add(c);
                                } catch (Exception ignored) {
                                }
                            });
                        }
                        Path out = Paths.get("target", "last-run-summary.json");
                        try (java.io.Writer w = Files.newBufferedWriter(out)) {
                            if (parts.size() == 1) {
                                // copy single test object
                                w.write(parts.get(0));
                            } else {
                                // write suite array
                                StringBuilder sb = new StringBuilder();
                                sb.append('{').append("\"suite\":").append('[');
                                for (int i = 0; i < parts.size(); i++) {
                                    if (i > 0)
                                        sb.append(',');
                                    sb.append(parts.get(i));
                                }
                                sb.append(']').append('}');
                                w.write(sb.toString());
                            }
                        }
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        }
    }

    // helpers for JSON serialization
    private static String jsonEscape(Object o) {
        if (o == null)
            return "null";
        String s = o.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return '"' + s + '"';
    }

    private static String listToJsonArray(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean first = true;
        for (String it : list) {
            if (!first)
                sb.append(',');
            else
                first = false;
            sb.append(jsonEscape(it));
        }
        sb.append(']');
        return sb.toString();
    }

    private String sanitizeFileName(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_. ]", "_");
    }
}
