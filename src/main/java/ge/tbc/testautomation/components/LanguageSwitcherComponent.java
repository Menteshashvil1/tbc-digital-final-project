package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LanguageSwitcherComponent extends BaseComponent {
    public final Locator toggle;
    public final Locator offeredLanguage;

    public LanguageSwitcherComponent(Page page, Locator scope) {
        super(page, scope.locator("tbcx-lang-switcher"));
        toggle = root.locator(".tbcx-language-select__field");
        offeredLanguage = root.locator(".tbcx-language-select__selected");
    }

    public void toggleLanguage() {
        toggle.click();
    }
}
