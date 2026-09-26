package ge.tbc.testautomation.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.regex.Pattern;

public class CurrencyDropdownComponent extends BaseComponent {
    public final Locator trigger;
    public final Locator selectedCurrency;
    public final Locator menu;

    public CurrencyDropdownComponent(Page page, String controlName) {
        super(page, page.locator("app-currency-dropdown[formcontrolname='" + controlName + "']"));
        trigger = root.locator(".currency-dropdown__trigger");
        selectedCurrency = root.locator(".currency-dropdown__selected");
        menu = page.locator(".currency-dropdown__panel .currency-dropdown__menu");
    }

    public Locator option(String iso) {
        return menu.locator(".currency-dropdown__item")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + iso + "\\s*$")));
    }

    public void open() {
        trigger.click();
    }

    public void choose(String iso) {
        open();
        option(iso).click();
    }
}
