package ge.tbc.testautomation.constants;

public final class TestConstants {
    private TestConstants() {
    }

    public static final String CTA_SECTION_TYPE = "ctaSection";
    public static final String NOT_FOUND_EXCEPTION = "ObjectNotFoundException";
    public static final String UNKNOWN_PAGE_ID = "tbcDigitalMissingPage404";

    public static final String GEL = "GEL";
    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final String GEL_SYMBOL = "₾";
    public static final String USD_SYMBOL = "$";
    public static final String DEFAULT_CONVERSION_AMOUNT = "100";
    public static final String CURRENCY_URL_PATTERN = "/%s-to-%s";
    public static final String AMOUNT_QUERY = "amount=";
    public static final String RATE_DESCRIPTION_PATTERN = "1 %s = %s %s";
    public static final double AMOUNT_TOLERANCE = 0.01;

    public static final String HTTP_GET = "GET";
    public static final int MAX_BREADCRUMBS = 5;
    public static final int MIN_PUBLISHED_PAGES = 50;
    public static final int MIN_COMMERCIAL_RATES = 5;
}
