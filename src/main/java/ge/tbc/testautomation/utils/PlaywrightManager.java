package ge.tbc.testautomation.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public final class PlaywrightManager {
    private static final Pattern THIRD_PARTY_TRAFFIC = Pattern.compile(
            "googletagmanager|google-analytics|analytics\\.google|doubleclick|googleadservices"
                    + "|google\\.com/(ccm|rmkt|pagead)|facebook|linkedin|posthog|medallia"
                    + "|visualwebsiteoptimizer|appsflyer|run\\.app/events");

    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final List<Playwright> STARTED = new CopyOnWriteArrayList<>();

    static {
        PlaywrightAssertions.setDefaultAssertionTimeout(ConfigReader.getInt("assertion.timeout"));
    }

    private PlaywrightManager() {
    }

    public static Page startPage() {
        BrowserContext context = browser().newContext(new Browser.NewContextOptions()
                .setViewportSize(ConfigReader.getInt("viewport.width"), ConfigReader.getInt("viewport.height"))
                .setLocale("en-US")
                .setTimezoneId("Asia/Tbilisi"));
        context.setDefaultTimeout(ConfigReader.getInt("default.timeout"));
        context.setDefaultNavigationTimeout(ConfigReader.getInt("navigation.timeout"));
        if (ConfigReader.getBoolean("block.third.party")) {
            context.route(THIRD_PARTY_TRAFFIC, Route::abort);
        }

        Page page = context.newPage();
        CONTEXT.set(context);
        PAGE.set(page);
        return page;
    }

    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("No page was started on thread " + Thread.currentThread().getName());
        }
        return page;
    }

    public static void closePage() {
        BrowserContext context = CONTEXT.get();
        if (context != null) {
            context.close();
        }
        CONTEXT.remove();
        PAGE.remove();
    }

    public static void shutdown() {
        STARTED.forEach(Playwright::close);
        STARTED.clear();
    }

    private static Browser browser() {
        Browser browser = BROWSER.get();
        if (browser != null && browser.isConnected()) {
            return browser;
        }
        Playwright playwright = Playwright.create();
        STARTED.add(playwright);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(ConfigReader.getBoolean("headless"))
                .setSlowMo(ConfigReader.getInt("slow.mo"));
        browser = browserType(playwright, ConfigReader.get("browser")).launch(options);
        BROWSER.set(browser);
        return browser;
    }

    private static BrowserType browserType(Playwright playwright, String name) {
        return switch (name.toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };
    }
}
