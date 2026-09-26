package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommercialRate(
        String iso,
        String name,
        double buyRate,
        double sellRate,
        double officialCourse,
        double weight,
        double diff
) {
}
