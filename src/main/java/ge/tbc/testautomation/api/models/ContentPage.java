package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ContentPage(
        @JsonProperty("$id") String id,
        @JsonProperty("$type") String type,
        String key,
        String slug,
        Seo seo,
        Breadcrumbs breadcrumbs,
        List<SectionComponent> sectionComponents
) {
    public Optional<SectionComponent> firstSectionOfType(String sectionType) {
        return sectionComponents.stream()
                .filter(section -> sectionType.equals(section.type()))
                .findFirst();
    }

    public List<String> breadcrumbLabels() {
        return breadcrumbs.items().stream()
                .map(Link::label)
                .map(String::trim)
                .toList();
    }
}
