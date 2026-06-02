package com.qa.enums;

import java.util.Arrays;

/**
 * Supported mobile platforms. Replaces the old "Android"/"iOS" string literals
 * so typos (e.g. the former "undid") become compile-time errors.
 */
public enum Platform {

    ANDROID,
    IOS;

    public static Platform from(String value) {
        for (Platform platform : values()) {
            if (platform.name().equalsIgnoreCase(value)) {
                return platform;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported platform: '" + value + "' (expected one of " + Arrays.toString(values()) + ")");
    }
}
