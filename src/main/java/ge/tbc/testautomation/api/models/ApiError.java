package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
        String type,
        String title,
        int status,
        String detail,
        String code,
        String traceId,
        String endpoint
) {
}
