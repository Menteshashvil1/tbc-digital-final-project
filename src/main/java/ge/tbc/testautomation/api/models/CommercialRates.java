package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommercialRates(
        List<CommercialRate> rates
) {
    public Optional<CommercialRate> rateFor(String iso) {
        return rates.stream()
                .filter(rate -> rate.iso().equals(iso))
                .findFirst();
    }
}
