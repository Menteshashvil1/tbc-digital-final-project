package ge.tbc.testautomation.utils;

import ge.tbc.testautomation.constants.SiteLocale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class LocalizationData {
    private static final String LOCATION = "testdata/localization/%s.properties";
    private static final Map<SiteLocale, LocalizationData> CACHE = new ConcurrentHashMap<>();

    private final SiteLocale locale;
    private final Properties values;

    private LocalizationData(SiteLocale locale, Properties values) {
        this.locale = locale;
        this.values = values;
    }

    public static LocalizationData forLocale(SiteLocale locale) {
        return CACHE.computeIfAbsent(locale, LocalizationData::load);
    }

    private static LocalizationData load(SiteLocale locale) {
        String file = LOCATION.formatted(locale.path());
        Properties properties = new Properties();
        try (InputStream stream = LocalizationData.class.getClassLoader().getResourceAsStream(file)) {
            if (stream == null) {
                throw new IllegalStateException("Localization file " + file + " was not found");
            }
            properties.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new LocalizationData(locale, properties);
    }

    public SiteLocale locale() {
        return locale;
    }

    public String get(String key) {
        String value = values.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Key '" + key + "' is missing for locale " + locale);
        }
        return value.trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public List<String> values(String... keys) {
        return Arrays.stream(keys)
                .map(this::get)
                .toList();
    }

    @Override
    public String toString() {
        return locale.name();
    }
}
