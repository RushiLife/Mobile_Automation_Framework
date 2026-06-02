package com.qa.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Central, read-only access to {@code config/config.properties}.
 * A matching {@code -Dkey=value} system property always wins over the file,
 * so CI can parameterize a run (e.g. {@code -Dplatform=iOS}) without code changes.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config/config.properties not found on the classpath");
            }
            PROPERTIES.load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load config/config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing config key: " + key);
        }
        return value.trim();
    }

    public static String get(String key, String defaultValue) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return PROPERTIES.getProperty(key, defaultValue).trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Resolves a credential without committing it: a {@code -Dkey} system property
     * wins, then the environment variable, then the config file. Throws if none set.
     */
    public static String getSecret(String propKey, String envVar) {
        String sys = System.getProperty(propKey);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String env = System.getenv(envVar);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String fileValue = PROPERTIES.getProperty(propKey);
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue.trim();
        }
        throw new IllegalStateException(
                "Missing credential '" + propKey + "': set -D" + propKey + " or env var " + envVar);
    }
}
