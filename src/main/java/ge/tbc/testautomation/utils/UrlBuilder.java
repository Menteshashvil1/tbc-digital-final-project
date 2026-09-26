package ge.tbc.testautomation.utils;

import ge.tbc.testautomation.constants.SiteLocale;

public final class UrlBuilder {
    private UrlBuilder() {
    }

    public static String pageUrl(SiteLocale locale, String slug) {
        return ConfigReader.baseUrl() + localizedPath(locale, slug);
    }

    public static String localizedPath(SiteLocale locale, String slug) {
        String normalized = slug == null || slug.equals("/") ? "" : slug;
        return "/" + locale.path() + normalized;
    }
}
