package ge.tbc.testautomation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SectionInputs(
        String key,
        String title,
        RichTextNode richTextTitle,
        String bodyText,
        boolean showList,
        boolean showBodyText,
        List<ListItem> list,
        List<Button> buttons
) {
    public static final String HEADING_ONE = "heading-1";

    public String headingText() {
        if (richTextTitle == null) {
            return title;
        }
        return richTextTitle.firstNodeOfType(HEADING_ONE)
                .map(RichTextNode::plainText)
                .map(String::trim)
                .orElse(title);
    }

    public List<String> listLabels() {
        return list.stream()
                .map(ListItem::label)
                .map(String::trim)
                .toList();
    }

    public Button primaryButton() {
        return buttons.get(0);
    }
}
