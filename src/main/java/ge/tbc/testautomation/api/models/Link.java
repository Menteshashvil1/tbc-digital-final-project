package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Link(
        String key,
        String label,
        String url,
        String target,
        boolean isExternal
) {
}
