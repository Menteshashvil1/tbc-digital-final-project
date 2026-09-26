package ge.tbc.testautomation.constants;

public final class Endpoints {
    private Endpoints() {
    }

    public static final String SITE = "/api/v1/sites/{siteId}";
    public static final String SITE_PAGE = "/api/v1/sites/pages/{pageId}";
    public static final String EXCHANGE_RATE = "/api/v1/exchangeRates/getExchangeRate";
    public static final String COMMERCIAL_RATES = "/api/v1/exchangeRates/commercialList";

    public static final String LOCALE_PARAM = "locale";
    public static final String FIRST_ISO_PARAM = "Iso1";
    public static final String SECOND_ISO_PARAM = "Iso2";
}
