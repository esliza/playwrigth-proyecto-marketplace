package com.example.screenplay.abilities;

import com.example.screenplay.core.Ability;
import com.microsoft.playwright.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class UsePlaywright implements Ability {
    private final Playwright playwright;
    private final Browser browser;
    private final BrowserContext context;
    private final Page page;
    private final boolean headless;
    private final Path recordVideoDirPath;

    public UsePlaywright(Playwright playwright, Browser browser, BrowserContext context, Page page, boolean headless,
            Path recordVideoDirPath) {
        this.playwright = playwright;
        this.browser = browser;
        this.context = context;
        this.page = page;
        this.headless = headless;
        this.recordVideoDirPath = recordVideoDirPath;
    }

    public static UsePlaywright createWithSystemProperties() {
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        int slowMo = Integer.parseInt(System.getProperty("playwright.slowMo", "0"));
        String channel = System.getProperty("playwright.channel", null);
        String recordDir = System.getProperty("playwright.recordVideoDir", null);
        String recordSize = System.getProperty("playwright.recordVideoSize", null); // e.g. 1280x720
        boolean enableRecording = Boolean.parseBoolean(System.getProperty("playwright.enableRecording", "false"));

        Playwright pw = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
        if (slowMo > 0)
            launchOptions.setSlowMo((double) slowMo);
        if (channel != null && !channel.isEmpty())
            launchOptions.setChannel(channel);

        Browser browser;
        try {
            browser = pw.chromium().launch(launchOptions);
        } catch (Exception e) {
            // fallback to managed chromium if channel failed
            browser = pw.chromium()
                    .launch(new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo((double) slowMo));
        }

        Browser.NewContextOptions ctxOptions = new Browser.NewContextOptions();
        Path recordPath = null;
        // Only enable Playwright video recording when the explicit flag is set
        // This avoids writing video files for every test when recording is not desired.
        if (enableRecording && recordDir != null && !recordDir.isEmpty()) {
            try {
                Path base = Paths.get(recordDir);
                Files.createDirectories(base);
                // create a unique subfolder per test/instance
                String unique = java.util.UUID.randomUUID().toString();
                recordPath = base.resolve(unique);
                Files.createDirectories(recordPath);
                ctxOptions.setRecordVideoDir(recordPath);
            } catch (Exception e) {
                System.out.println("[UsePlaywright] failed to create record video dir: " + e.getMessage());
                recordPath = null;
            }
            // Optional `playwright.recordVideoSize` is available in some Playwright
            // Java versions; if present we intentionally skip size parsing here to
            // keep compatibility. Set video size via system-level context config
            // if your SDK supports it.
        }
        BrowserContext ctx = browser.newContext(ctxOptions);
        Page page = ctx.newPage();

        return new UsePlaywright(pw, browser, ctx, page, headless, recordPath);
    }

    public static UsePlaywright createWithSystemProperties(String testDisplayName) {
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        int slowMo = Integer.parseInt(System.getProperty("playwright.slowMo", "0"));
        String channel = System.getProperty("playwright.channel", null);
        String recordDir = System.getProperty("playwright.recordVideoDir", null);
        String recordSize = System.getProperty("playwright.recordVideoSize", null); // e.g. 1280x720
        boolean enableRecording = Boolean.parseBoolean(System.getProperty("playwright.enableRecording", "false"));

        Playwright pw = Playwright.create();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
        if (slowMo > 0)
            launchOptions.setSlowMo((double) slowMo);
        if (channel != null && !channel.isEmpty())
            launchOptions.setChannel(channel);

        Browser browser;
        try {
            browser = pw.chromium().launch(launchOptions);
        } catch (Exception e) {
            // fallback to managed chromium if channel failed
            browser = pw.chromium()
                    .launch(new BrowserType.LaunchOptions().setHeadless(headless).setSlowMo((double) slowMo));
        }

        Browser.NewContextOptions ctxOptions = new Browser.NewContextOptions();
        Path recordPath = null;
        // Only enable Playwright video recording when the explicit flag is set
        // This avoids writing video files for every test when recording is not desired.
        if (enableRecording && recordDir != null && !recordDir.isEmpty()) {
            try {
                Path base = Paths.get(recordDir);
                Files.createDirectories(base);
                // create a subfolder per test using the sanitized display name if available
                String safeName = (testDisplayName != null && !testDisplayName.isEmpty())
                        ? sanitizeFileName(testDisplayName)
                        : null;
                String uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 8);
                String folderName = (safeName != null) ? safeName + "-" + uniqueSuffix : uniqueSuffix;
                recordPath = base.resolve(folderName);
                Files.createDirectories(recordPath);
                ctxOptions.setRecordVideoDir(recordPath);

                // If a record size is provided (format WxH), try to set it via reflection
                if (recordSize != null && recordSize.contains("x")) {
                    try {
                        String[] parts = recordSize.split("x");
                        int w = Integer.parseInt(parts[0].trim());
                        int h = Integer.parseInt(parts[1].trim());
                        try {
                            java.lang.reflect.Method m = Browser.NewContextOptions.class
                                    .getMethod("setRecordVideoSize", int.class, int.class);
                            m.invoke(ctxOptions, w, h);
                        } catch (NoSuchMethodException ignored) {
                            // method not available on this Playwright Java version
                        }
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception e) {
                System.out.println("[UsePlaywright] failed to create record video dir: " + e.getMessage());
                recordPath = null;
            }
        }
        BrowserContext ctx = browser.newContext(ctxOptions);
        Page page = ctx.newPage();

        return new UsePlaywright(pw, browser, ctx, page, headless, recordPath);
    }

    public Playwright playwright() {
        return playwright;
    }

    public Browser browser() {
        return browser;
    }

    public BrowserContext context() {
        return context;
    }

    public Page page() {
        return page;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void close() {
        try {
            context.close();
        } catch (Exception ignored) {
        }
        try {
            browser.close();
        } catch (Exception ignored) {
        }
        try {
            playwright.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Delete any recorded video files in the configured Playwright record dir.
     * Useful to remove artifacts when tests pass and we don't want to keep videos.
     */
    public void clearRecordedVideos() {
        if (recordVideoDirPath == null)
            return;
        try (java.util.stream.Stream<Path> files = Files.list(recordVideoDirPath)) {
            files.forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * Move any recorded video files from the record video dir into
     * `baseTargetDir/<testName>/` and prefix filenames with the test name.
     * Useful to collect artifacts after test run.
     */
    public java.util.List<Path> moveRecordedVideos(String baseTargetDir, String testName) {
        java.util.List<Path> moved = new java.util.ArrayList<>();
        if (recordVideoDirPath == null)
            return moved;
        final String finalTestName = (testName == null || testName.isEmpty()) ? "unnamed-test" : testName;
        try {
            Path target = Paths.get(baseTargetDir, sanitizeFileName(finalTestName));
            Files.createDirectories(target);
            try (java.util.stream.Stream<Path> files = Files.list(recordVideoDirPath)) {
                files.forEach(p -> {
                    String name = p.getFileName().toString();
                    String destName = sanitizeFileName(finalTestName) + "-" + name;
                    Path dest = target.resolve(destName);
                    boolean ok = tryMoveWithRetries(p, dest, 5, 200);
                    if (ok) {
                        moved.add(dest);
                    } else {
                        System.out.println(
                                "[UsePlaywright] warning: failed to move video after retries: " + p.toString());
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("[UsePlaywright] moveRecordedVideos error: " + e.getMessage());
        }
        return moved;
    }

    private boolean tryMoveWithRetries(Path src, Path dest, int maxAttempts, long initialDelayMs) {
        int attempt = 0;
        long delay = initialDelayMs;
        while (attempt < maxAttempts) {
            attempt++;
            try {
                // Attempt atomic move (replace if exists)
                Files.move(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception e) {
                // If last attempt, break and return false after logging
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                }
                delay = delay * 2; // exponential backoff
            }
        }
        return false;
    }

    private static String sanitizeFileName(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_. ]", "_");
    }
}