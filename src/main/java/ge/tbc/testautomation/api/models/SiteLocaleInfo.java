package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SiteLocaleInfo(
        String id,
        String key,
        String code,
        String shortCode,
        String label,
        boolean isDefault
) {
}
