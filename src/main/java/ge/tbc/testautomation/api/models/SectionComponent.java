package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SectionComponent(
        @JsonProperty("$id") String id,
        String key,
        String type,
        SectionInputs inputs
) {
}
