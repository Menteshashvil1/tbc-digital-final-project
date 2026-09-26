package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.components.CtaSectionComponent;

public class ConsumerLoanTermsPage extends BasePage {
    public final CtaSectionComponent heroSection;
    public final Locator termsTabs;

    public ConsumerLoanTermsPage(Page page) {
        super(page);
        heroSection = new CtaSectionComponent(page, 0);
        termsTabs = page.locator("app-tabs-section");
    }
}
