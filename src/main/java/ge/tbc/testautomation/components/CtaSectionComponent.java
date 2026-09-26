package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CtaSectionComponent extends BaseComponent {
    public final Locator title;
    public final Locator bodyText;
    public final Locator listItems;
    public final Locator buttons;

    public CtaSectionComponent(Page page, int index) {
        super(page, page.locator("tbcx-pw-cta-section").nth(index));
        title = root.locator(".tbcx-pw-cta-section__info__title");
        bodyText = root.locator(".tbcx-pw-cta-section__info__text");
        listItems = root.locator("tbcx-pw-list .tbcx-list-item__text");
        buttons = root.locator(".tbcx-pw-cta-section__info__buttons-wrapper a");
    }

    public Locator button(int index) {
        return buttons.nth(index);
    }

    public void clickButton(int index) {
        button(index).click();
    }
}
