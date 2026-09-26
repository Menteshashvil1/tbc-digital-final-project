package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage extends BasePage {
    public final Locator pageContent;

    public HomePage(Page page) {
        super(page);
        pageContent = page.locator("app-page");
    }
}
