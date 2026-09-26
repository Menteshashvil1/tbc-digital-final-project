package ge.tbc.testautomation.api.clients;

import ge.tbc.testautomation.api.models.CommercialRates;
import ge.tbc.testautomation.api.models.ExchangeRate;
import ge.tbc.testautomation.constants.Endpoints;
import ge.tbc.testautomation.constants.SiteLocale;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

public class ExchangeRateClient extends BaseApiClient {

    @Step("GET exchange rate {from} -> {to}")
    public Response getExchangeRateResponse(String from, String to) {
        return request()
                .queryParam(Endpoints.FIRST_ISO_PARAM, from)
                .queryParam(Endpoints.SECOND_ISO_PARAM, to)
                .get(Endpoints.EXCHANGE_RATE);
    }

    public ExchangeRate getExchangeRate(String from, String to) {
        return getExchangeRateResponse(from, to)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(ExchangeRate.class);
    }

    @Step("GET commercial exchange rates for locale {locale}")
    public Response getCommercialRatesResponse(SiteLocale locale) {
        return request()
                .queryParam(Endpoints.LOCALE_PARAM, locale.code())
                .get(Endpoints.COMMERCIAL_RATES);
    }

    public CommercialRates getCommercialRates(SiteLocale locale) {
        return getCommercialRatesResponse(locale)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CommercialRates.class);
    }
}
