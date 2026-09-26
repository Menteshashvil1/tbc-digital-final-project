package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.steps.CurrencyExchangeSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Currency exchange")
public class CurrencyCalculatorTest extends BaseUiTest {

    @Test(description = "TBC-T5 | Swapping currencies reverses the conversion direction and recalculates the amount")
    public void swappingCurrenciesShouldReverseConversion() {
        CurrencyExchangeSteps exchangeSteps = new CurrencyExchangeSteps(page());
        new NavigationSteps(page()).openPage(SiteLocale.EN, PageSlugs.CURRENCY_EXCHANGE);

        exchangeSteps
                .calculatorShouldBeReady(TestConstants.USD, TestConstants.GEL)
                .swapCurrencies(TestConstants.GEL)
                .calculatorShouldBeReady(TestConstants.GEL, TestConstants.USD)
                .sellSymbolShouldBe(TestConstants.GEL_SYMBOL)
                .buySymbolShouldBe(TestConstants.USD_SYMBOL);

        double quotedRate = exchangeSteps.quotedRate();

        exchangeSteps
                .convertedAmountShouldBe(Double.parseDouble(TestConstants.DEFAULT_CONVERSION_AMOUNT), quotedRate)
                .urlShouldDescribeConversion(TestConstants.GEL, TestConstants.USD, TestConstants.DEFAULT_CONVERSION_AMOUNT);
    }
}
