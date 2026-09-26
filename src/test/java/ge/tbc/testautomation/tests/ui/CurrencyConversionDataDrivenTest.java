package ge.tbc.testautomation.tests.ui;

import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.database.models.CurrencyConversion;
import ge.tbc.testautomation.steps.CurrencyExchangeSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import ge.tbc.testautomation.utils.DataProviders;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Currency exchange")
public class CurrencyConversionDataDrivenTest extends BaseUiTest {

    @Test(description = "TBC-T3 | Currency calculator converts database-driven amounts with the quoted rate",
            dataProvider = DataProviders.CURRENCY_CONVERSIONS, dataProviderClass = DataProviders.class)
    public void calculatorShouldConvertStoredAmount(CurrencyConversion conversion) {
        CurrencyExchangeSteps exchangeSteps = new CurrencyExchangeSteps(page());
        new NavigationSteps(page()).openPage(SiteLocale.EN, PageSlugs.CURRENCY_EXCHANGE);

        exchangeSteps
                .calculatorShouldBeReady(TestConstants.USD, TestConstants.GEL)
                .selectSellCurrency(conversion.getSellCurrency())
                .sellSymbolShouldBe(conversion.getSellSymbol())
                .rateDescriptionShouldQuote(conversion.getSellCurrency(), conversion.getBuyCurrency())
                .enterSellAmount(conversion.amountAsText());

        double quotedRate = exchangeSteps.quotedRate();

        exchangeSteps
                .convertedAmountShouldBe(conversion.getAmount().doubleValue(), quotedRate)
                .urlShouldDescribeConversion(conversion.getSellCurrency(), conversion.getBuyCurrency(),
                        conversion.amountAsText());
    }
}
