package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Seo(
        String key,
        String title,
        String description,
        boolean noIndex,
        boolean noFollow
) {
}
