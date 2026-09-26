package ge.tbc.testautomation.steps;

import ge.tbc.testautomation.api.clients.ExchangeRateClient;
import ge.tbc.testautomation.api.models.CommercialRate;
import ge.tbc.testautomation.api.models.CommercialRates;
import ge.tbc.testautomation.api.models.ExchangeRate;
import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.constants.TestConstants;
import io.qameta.allure.Step;
import org.testng.Assert;

public class ExchangeRateApiSteps {
    private final ExchangeRateClient client = new ExchangeRateClient();

    @Step("Load commercial rates in {locale}")
    public CommercialRates loadCommercialRates(SiteLocale locale) {
        return client.getCommercialRates(locale);
    }

    @Step("Load the {from} -> {to} exchange rate")
    public ExchangeRate loadExchangeRate(String from, String to) {
        return client.getExchangeRate(from, to);
    }

    @Step("Commercial list should quote {isoCodes}")
    public ExchangeRateApiSteps commercialListShouldQuote(CommercialRates rates, String... isoCodes) {
        Assert.assertTrue(rates.rates().size() >= TestConstants.MIN_COMMERCIAL_RATES,
                "Commercial list is unexpectedly short: " + rates.rates().size());
        for (String iso : isoCodes) {
            CommercialRate rate = rates.rateFor(iso)
                    .orElseThrow(() -> new AssertionError("Commercial list has no " + iso));
            Assert.assertFalse(rate.name().isBlank(), iso + " should have a display name");
        }
        rates.rates().forEach(rate -> {
            Assert.assertTrue(rate.buyRate() > 0, rate.iso() + " buy rate should be positive");
            Assert.assertTrue(rate.sellRate() >= rate.buyRate(), rate.iso() + " sell rate should not be below buy rate");
            Assert.assertTrue(rate.officialCourse() >= rate.buyRate() && rate.officialCourse() <= rate.sellRate(),
                    rate.iso() + " official rate should sit between the bank's buy and sell rates");
            Assert.assertTrue(rate.weight() > 0, rate.iso() + " weight should be positive");
        });
        return this;
    }

    @Step("Direct rate should match the commercial list for {from}")
    public ExchangeRateApiSteps directRateShouldMatchList(ExchangeRate direct, CommercialRates rates, String from, String to) {
        CommercialRate listed = rates.rateFor(from).orElseThrow();
        Assert.assertEquals(direct.iso1(), from, "Direct rate source currency");
        Assert.assertEquals(direct.iso2(), to, "Direct rate target currency");
        Assert.assertEquals(direct.buyRate(), listed.buyRate(), TestConstants.AMOUNT_TOLERANCE / 100,
                "Buy rate differs between endpoints");
        Assert.assertEquals(direct.sellRate(), listed.sellRate(), TestConstants.AMOUNT_TOLERANCE / 100,
                "Sell rate differs between endpoints");
        Assert.assertEquals(direct.currencyWeight(), listed.weight(), "Currency weight differs between endpoints");
        return this;
    }

    @Step("Localized commercial lists should agree on rates")
    public ExchangeRateApiSteps localizedListsShouldAgree(CommercialRates english, CommercialRates georgian, String iso) {
        CommercialRate en = english.rateFor(iso).orElseThrow();
        CommercialRate ka = georgian.rateFor(iso).orElseThrow();
        Assert.assertEquals(en.buyRate(), ka.buyRate(), "Rates must not depend on locale");
        Assert.assertNotEquals(en.name(), ka.name(), "Currency name should be translated");
        return this;
    }
}
