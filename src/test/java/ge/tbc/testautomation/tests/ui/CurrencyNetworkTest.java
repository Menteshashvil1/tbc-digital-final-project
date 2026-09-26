package ge.tbc.testautomation.tests.ui;

import com.microsoft.playwright.Response;
import ge.tbc.testautomation.api.models.ExchangeRate;
import ge.tbc.testautomation.constants.Endpoints;
import ge.tbc.testautomation.constants.PageSlugs;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.steps.CurrencyExchangeSteps;
import ge.tbc.testautomation.steps.NavigationSteps;
import ge.tbc.testautomation.tests.BaseUiTest;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

@Feature("Network validation")
public class CurrencyNetworkTest extends BaseUiTest {

    @Test(description = "TBC-T4 | Selecting a currency requests its rate from the exchange API and renders it")
    public void selectingCurrencyShouldRequestAndRenderRate() {
        CurrencyExchangeSteps exchangeSteps = new CurrencyExchangeSteps(page());
        new NavigationSteps(page()).openPage(SiteLocale.EN, PageSlugs.CURRENCY_EXCHANGE);
        exchangeSteps.calculatorShouldBeReady(TestConstants.USD, TestConstants.GEL);

        Response response = exchangeSteps.selectSellCurrencyCapturingRate(TestConstants.EUR);

        ExchangeRate rate = exchangeSteps
                .requestShouldTarget(response, Endpoints.EXCHANGE_RATE, TestConstants.EUR, TestConstants.GEL)
                .responseShouldContainRate(response, HttpStatus.SC_OK, TestConstants.EUR, TestConstants.GEL);

        exchangeSteps
                .rateDescriptionShouldShow(TestConstants.EUR, rate.buyRate(), TestConstants.GEL)
                .convertedAmountShouldBe(Double.parseDouble(TestConstants.DEFAULT_CONVERSION_AMOUNT), rate.buyRate())
                .urlShouldDescribeConversion(TestConstants.EUR, TestConstants.GEL, TestConstants.DEFAULT_CONVERSION_AMOUNT);
    }
}
