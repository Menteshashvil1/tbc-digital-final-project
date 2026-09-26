package ge.tbc.testautomation.steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Response;
import ge.tbc.testautomation.api.models.ExchangeRate;
import ge.tbc.testautomation.constants.Endpoints;
import ge.tbc.testautomation.constants.TestConstants;
import ge.tbc.testautomation.pages.CurrencyExchangePage;
import ge.tbc.testautomation.utils.AmountParser;
import ge.tbc.testautomation.utils.JsonMapper;
import ge.tbc.testautomation.utils.Patterns;
import io.qameta.allure.Step;
import org.testng.Assert;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CurrencyExchangeSteps {
    private static final Pattern RATE_VALUE = Pattern.compile("=\\s*([0-9]+(?:\\.[0-9]+)?)");

    private final Page page;
    private final CurrencyExchangePage exchangePage;

    public CurrencyExchangeSteps(Page page) {
        this.page = page;
        this.exchangePage = new CurrencyExchangePage(page);
    }

    @Step("Calculator should be ready with {from} -> {to}")
    public CurrencyExchangeSteps calculatorShouldBeReady(String from, String to) {
        assertThat(exchangePage.sellCurrency.selectedCurrency).hasText(from);
        assertThat(exchangePage.buyCurrency.selectedCurrency).hasText(to);
        assertThat(exchangePage.rateDescription).hasText(ratePattern(from, to));
        return this;
    }

    @Step("Select {iso} as the currency to sell")
    public CurrencyExchangeSteps selectSellCurrency(String iso) {
        if (exchangePage.sellCurrency.selectedCurrency.innerText().trim().equals(iso)) {
            return this;
        }
        page.waitForResponse(response -> isExchangeRateResponse(response, iso),
                () -> exchangePage.sellCurrency.choose(iso));
        assertThat(exchangePage.sellCurrency.selectedCurrency).hasText(iso);
        return this;
    }

    @Step("Select {iso} to sell and capture the exchange rate call")
    public Response selectSellCurrencyCapturingRate(String iso) {
        Response response = page.waitForResponse(candidate -> isExchangeRateResponse(candidate, iso),
                () -> exchangePage.sellCurrency.choose(iso));
        assertThat(exchangePage.sellCurrency.selectedCurrency).hasText(iso);
        return response;
    }

    @Step("Swap the sell and buy currencies")
    public CurrencyExchangeSteps swapCurrencies(String newSellCurrency) {
        page.waitForResponse(response -> isExchangeRateResponse(response, newSellCurrency),
                exchangePage.swapButton::click);
        return this;
    }

    @Step("Buy currency symbol should be {symbol}")
    public CurrencyExchangeSteps buySymbolShouldBe(String symbol) {
        assertThat(exchangePage.buyCurrencySymbol).hasText(symbol);
        return this;
    }

    @Step("Enter {amount} as the amount to sell")
    public CurrencyExchangeSteps enterSellAmount(String amount) {
        exchangePage.sellAmountInput.fill(amount);
        assertThat(exchangePage.sellAmountInput).hasValue(amount);
        return this;
    }

    @Step("Sell currency symbol should be {symbol}")
    public CurrencyExchangeSteps sellSymbolShouldBe(String symbol) {
        assertThat(exchangePage.sellCurrencySymbol).hasText(symbol);
        return this;
    }

    @Step("Rate description should quote {from} in {to}")
    public CurrencyExchangeSteps rateDescriptionShouldQuote(String from, String to) {
        assertThat(exchangePage.rateDescription).hasText(ratePattern(from, to));
        return this;
    }

    @Step("Rate description should show {rate}")
    public CurrencyExchangeSteps rateDescriptionShouldShow(String from, double rate, String to) {
        assertThat(exchangePage.rateDescription).hasText(
                TestConstants.RATE_DESCRIPTION_PATTERN.formatted(from, AmountParser.plain(rate), to));
        return this;
    }

    public double quotedRate() {
        String description = exchangePage.rateDescription.innerText();
        Matcher matcher = RATE_VALUE.matcher(description);
        Assert.assertTrue(matcher.find(), "Rate description has no rate: " + description);
        return Double.parseDouble(matcher.group(1));
    }

    @Step("Converted amount should equal {amount} x {rate}")
    public CurrencyExchangeSteps convertedAmountShouldBe(double amount, double rate) {
        String expected = AmountParser.plain(AmountParser.convert(amount, rate));
        assertThat(exchangePage.buyAmountInput).hasValue(expected);
        return this;
    }

    @Step("URL should describe {amount} {from} -> {to}")
    public CurrencyExchangeSteps urlShouldDescribeConversion(String from, String to, String amount) {
        assertThat(page).hasURL(Patterns.endsWith(TestConstants.CURRENCY_URL_PATTERN.formatted(from, to)
                + "?" + TestConstants.AMOUNT_QUERY + amount));
        return this;
    }

    @Step("Captured request should be GET {endpoint} with {from} -> {to}")
    public CurrencyExchangeSteps requestShouldTarget(Response response, String endpoint, String from, String to) {
        Request request = response.request();
        URI uri = URI.create(request.url());
        Map<String, String> query = queryParameters(uri);

        Assert.assertEquals(request.method(), TestConstants.HTTP_GET, "Unexpected HTTP method");
        Assert.assertEquals(uri.getPath(), endpoint, "Unexpected endpoint");
        Assert.assertEquals(query.get(Endpoints.FIRST_ISO_PARAM), from, "Unexpected source currency parameter");
        Assert.assertEquals(query.get(Endpoints.SECOND_ISO_PARAM), to, "Unexpected target currency parameter");
        return this;
    }

    @Step("Captured response should be {status} with a {from} -> {to} rate")
    public ExchangeRate responseShouldContainRate(Response response, int status, String from, String to) {
        Assert.assertEquals(response.status(), status, "Unexpected response status");
        ExchangeRate rate = JsonMapper.fromJson(response.text(), ExchangeRate.class);
        Assert.assertEquals(rate.iso1(), from, "Response describes another source currency");
        Assert.assertEquals(rate.iso2(), to, "Response describes another target currency");
        Assert.assertTrue(rate.buyRate() > 0, "Buy rate should be positive");
        Assert.assertTrue(rate.sellRate() >= rate.buyRate(), "Bank sell rate should not be below its buy rate");
        return rate;
    }

    public static boolean isExchangeRateResponse(Response response, String from) {
        String url = response.url();
        return url.contains(Endpoints.EXCHANGE_RATE)
                && url.contains(Endpoints.FIRST_ISO_PARAM + "=" + from);
    }

    private static Pattern ratePattern(String from, String to) {
        return Pattern.compile("^\\s*1 " + from + " = [0-9]+(\\.[0-9]+)? " + to + "\\s*$");
    }

    private static Map<String, String> queryParameters(URI uri) {
        return Arrays.stream(uri.getQuery().split("&"))
                .map(pair -> pair.split("=", 2))
                .collect(Collectors.toMap(pair -> pair[0], pair -> pair.length > 1 ? pair[1] : ""));
    }
}
