package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CookieBannerComponent extends BaseComponent {
    public final Locator title;
    public final Locator description;
    public final Locator actionButtons;
    public final Locator acceptAllButton;
    public final Locator rejectAllButton;

    public CookieBannerComponent(Page page) {
        super(page, page.locator(".tbcx-pw-cookie-consent"));
        title = root.locator(".tbcx-pw-cookie-consent__title");
        description = root.locator(".tbcx-pw-cookie-consent__description");
        actionButtons = root.locator(".tbcx-pw-cookie-consent__actions button");
        acceptAllButton = actionButtons.first();
        rejectAllButton = actionButtons.last();
    }

    public void acceptAll() {
        acceptAllButton.click();
    }

    public void rejectAll() {
        rejectAllButton.click();
    }

    public void rejectWheneverShown() {
        page.addLocatorHandler(root, banner -> rejectAll());
    }
}
