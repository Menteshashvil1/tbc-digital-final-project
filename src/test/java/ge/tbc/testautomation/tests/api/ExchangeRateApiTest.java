package ge.tbc.testautomation.tests.api;

import ge.tbc.testautomation.api.models.CommercialRates;
import ge.tbc.testautomation.api.models.ExchangeRate;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.steps.ExchangeRateApiSteps;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Exchange rate API")
public class ExchangeRateApiTest {
    private final ExchangeRateApiSteps apiSteps = new ExchangeRateApiSteps();

    @Test(description = "TBC-T11 | Commercial rate list and direct rate endpoint agree on the EUR to GEL rate")
    public void directRateShouldMatchCommercialList() {
        CommercialRates english = apiSteps.loadCommercialRates(SiteLocale.EN);
        CommercialRates georgian = apiSteps.loadCommercialRates(SiteLocale.KA);
        ExchangeRate direct = apiSteps.loadExchangeRate(TestConstants.EUR, TestConstants.GEL);

        apiSteps
                .commercialListShouldQuote(english, TestConstants.USD, TestConstants.EUR)
                .directRateShouldMatchList(direct, english, TestConstants.EUR, TestConstants.GEL)
                .localizedListsShouldAgree(english, georgian, TestConstants.EUR);
    }
}
