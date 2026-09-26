package ge.tbc.testautomation.utils;

import java.util.regex.Pattern;

public final class Patterns {
    private static final Pattern SPECIAL_CHARACTERS = Pattern.compile("[\\\\^$.|?*+()\\[\\]{}/-]");

    private Patterns() {
    }

    public static String literal(String text) {
        return SPECIAL_CHARACTERS.matcher(text).replaceAll("\\\\$0");
    }

    public static Pattern endsWith(String text) {
        return Pattern.compile(literal(text) + "$");
    }

    public static Pattern pageUrl(String url) {
        return Pattern.compile("^" + literal(url) + "/?(\\?.*)?$");
    }

    public static Pattern exactText(String text) {
        return Pattern.compile("^\\s*" + literal(text) + "\\s*$");
    }
}
