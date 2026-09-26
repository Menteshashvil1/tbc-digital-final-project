package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Site(
        @JsonProperty("$id") String id,
        @JsonProperty("$type") String type,
        String key,
        String country,
        List<SiteLocaleInfo> locales,
        List<SitePage> pages,
        String headerId,
        String footerId,
        String cookieConsentId,
        String notFoundPageId
) {
    public Optional<SitePage> pageBySlug(String slug) {
        return pages.stream()
                .filter(page -> page.slug().equals(slug))
                .findFirst();
    }

    public Optional<SiteLocaleInfo> localeByCode(String code) {
        return locales.stream()
                .filter(locale -> locale.code().equals(code))
                .findFirst();
    }
}
