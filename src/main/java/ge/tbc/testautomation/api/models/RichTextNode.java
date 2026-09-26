package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RichTextNode(
        String nodeType,
        String value,
        List<RichTextNode> content
) {
    public String plainText() {
        if (value != null) {
            return value;
        }
        if (content == null) {
            return "";
        }
        return content.stream()
                .map(RichTextNode::plainText)
                .collect(Collectors.joining());
    }

    public Optional<RichTextNode> firstNodeOfType(String type) {
        if (type.equals(nodeType)) {
            return Optional.of(this);
        }
        if (content == null) {
            return Optional.empty();
        }
        return content.stream()
                .map(child -> child.firstNodeOfType(type))
                .flatMap(Optional::stream)
                .findFirst();
    }
}
