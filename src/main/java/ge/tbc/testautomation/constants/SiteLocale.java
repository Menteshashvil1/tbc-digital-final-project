package ge.tbc.testautomation.constants;

import java.util.Arrays;

public enum SiteLocale {
    EN("en", "en-US"),
    KA("ka", "ka-GE");

    private final String path;
    private final String code;

    SiteLocale(String path, String code) {
        this.path = path;
        this.code = code;
    }

    public String path() {
        return path;
    }

    public String code() {
        return code;
    }

    public SiteLocale alternative() {
        return Arrays.stream(values())
                .filter(locale -> locale != this)
                .findFirst()
                .orElseThrow();
    }

    public static SiteLocale fromPath(String path) {
        return Arrays.stream(values())
                .filter(locale -> locale.path.equalsIgnoreCase(path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported locale: " + path));
    }
}
