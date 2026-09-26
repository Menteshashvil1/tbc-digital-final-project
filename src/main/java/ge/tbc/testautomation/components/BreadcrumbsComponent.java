package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class BreadcrumbsComponent extends BaseComponent {
    public final Locator items;
    public final Locator links;

    public BreadcrumbsComponent(Page page) {
        super(page, page.locator("tbcx-pw-breadcrumbs"));
        items = root.locator(".tbcx-pw-breadcrumbs__item");
        links = items.locator("a");
    }

    public Locator link(int index) {
        return links.nth(index);
    }
}
