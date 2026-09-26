package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExchangeRate(
        String iso1,
        String iso2,
        double buyRate,
        double sellRate,
        int conversionType,
        double currencyWeight,
        String updateDate
) {
}
