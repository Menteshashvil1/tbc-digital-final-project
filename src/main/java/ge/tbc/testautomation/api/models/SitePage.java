package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SitePage(
        String id,
        String key,
        String slug,
        boolean isDynamic
) {
}
