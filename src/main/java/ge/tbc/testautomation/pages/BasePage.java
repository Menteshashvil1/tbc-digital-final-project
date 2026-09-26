package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.components.BreadcrumbsComponent;
import ge.tbc.testautomation.components.CookieBannerComponent;
import ge.tbc.testautomation.components.HeaderComponent;

public abstract class BasePage {
    protected final Page page;

    public final HeaderComponent header;
    public final CookieBannerComponent cookieBanner;
    public final BreadcrumbsComponent breadcrumbs;
    public final Locator html;
    public final Locator firewallMessage;

    protected BasePage(Page page) {
        this.page = page;
        header = new HeaderComponent(page);
        cookieBanner = new CookieBannerComponent(page);
        breadcrumbs = new BreadcrumbsComponent(page);
        html = page.locator("html");
        firewallMessage = page.locator("body > pre");
    }

    public Page page() {
        return page;
    }
}
