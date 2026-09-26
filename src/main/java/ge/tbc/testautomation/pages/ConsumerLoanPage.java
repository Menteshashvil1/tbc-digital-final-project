package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Page;
import ge.tbc.testautomation.components.CtaSectionComponent;

public class ConsumerLoanPage extends BasePage {
    public final CtaSectionComponent heroSection;

    public ConsumerLoanPage(Page page) {
        super(page);
        heroSection = new CtaSectionComponent(page, 0);
    }
}
