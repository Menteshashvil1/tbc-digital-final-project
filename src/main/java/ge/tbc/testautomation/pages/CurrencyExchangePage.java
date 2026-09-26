package ge.tbc.testautomation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import ge.tbc.testautomation.components.CurrencyDropdownComponent;

public class CurrencyExchangePage extends BasePage {
    public final Locator calculator;
    public final Locator sellAmountInput;
    public final Locator buyAmountInput;
    public final Locator sellCurrencySymbol;
    public final Locator buyCurrencySymbol;
    public final Locator rateDescription;
    public final Locator swapButton;
    public final CurrencyDropdownComponent sellCurrency;
    public final CurrencyDropdownComponent buyCurrency;

    public CurrencyExchangePage(Page page) {
        super(page);
        calculator = page.locator(".currency-calculator");
        sellAmountInput = calculator.locator("#sell-amount");
        buyAmountInput = calculator.locator("#buy-amount");
        sellCurrencySymbol = calculator.locator("app-amount-field[formcontrolname='sellAmount'] .amount-field__currency");
        buyCurrencySymbol = calculator.locator("app-amount-field[formcontrolname='buyAmount'] .amount-field__currency");
        rateDescription = page.locator("p.exchange-rates-calculator__description");
        swapButton = calculator.locator(".currency-calculator__swap__button");
        sellCurrency = new CurrencyDropdownComponent(page, "sellCurrency");
        buyCurrency = new CurrencyDropdownComponent(page, "buyCurrency");
    }
}
