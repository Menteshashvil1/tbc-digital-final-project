package ge.tbc.testautomation.utils;

import ge.tbc.testautomation.constants.SiteLocale;
import ge.tbc.testautomation.database.CurrencyConversionRepository;
import org.testng.annotations.DataProvider;

import java.util.Arrays;

public final class DataProviders {
    public static final String LOCALES = "locales";
    public static final String CURRENCY_CONVERSIONS = "currencyConversions";

    private DataProviders() {
    }

    @DataProvider(name = LOCALES, parallel = true)
    public static Object[][] locales() {
        return Arrays.stream(SiteLocale.values())
                .map(locale -> new Object[]{LocalizationData.forLocale(locale)})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = CURRENCY_CONVERSIONS, parallel = true)
    public static Object[][] currencyConversions() {
        return new CurrencyConversionRepository().findActive().stream()
                .map(conversion -> new Object[]{conversion})
                .toArray(Object[][]::new);
    }
}
